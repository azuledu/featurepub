package es.uva.idelab.featurepub.service;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.geotools.data.Query;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import es.uva.idelab.featurepub.Producer;
import es.uva.idelab.featurepub.encoder.Encoder;
import es.uva.idelab.featurepub.process.Process;
import es.uva.idelab.featurepub.process.Styles;

public class DispatcherServlet extends HttpServlet {

	private static final Log logger = LogFactory.getLog(DispatcherServlet.class);
	WebApplicationContext appContext;
	
	Query query;
	List<Process> processes;
	String stylesName;
	Encoder encoder;
	Producer producer;

	private String outFile = "featurePub.kml";

	public DispatcherServlet() {
		super();
	}
	
	public void init() {
		ServletContext servletContext = this.getServletContext();
		
		String prefix = servletContext.getRealPath("/");
		String file = getInitParameter("log4j-init-file");
		// if the log4j-init-file is not set, then no point in trying
		if (file != null) {
			PropertyConfigurator.configure(prefix + file);
		}
	
		
		appContext =  WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		
		producer = (Producer) appContext.getBean("producer");
		encoder = producer.getThematicEncoder();
	}

	public void reloadConf() {
		((ConfigurableApplicationContext) appContext).refresh();
		this.producer = (Producer) appContext.getBean("producer");
		encoder = producer.getThematicEncoder();
	}

	public void setOutFile(String outFile) {
		this.outFile = outFile;
	}

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String form_action = request.getParameter("form_action");
		if ("ReloadConf".equalsIgnoreCase(form_action)) {
			reloadConf();
			response.sendRedirect(response.encodeRedirectURL("/FeaturePub"));
			return;
		}

		this.query = (Query) appContext.getBean(request.getParameter("queryName") + "Query");
		this.processes = (List<Process>) appContext.getBean(request.getParameter("queryName") + "Process");
		this.stylesName = request.getParameter("queryName") + ".sld";

		if (logger.isDebugEnabled()) {
			logger.debug("doGet() - Request received");
			logger.debug("org.geotools.referencing.forceXY Property: " + System.getProperty("org.geotools.referencing.forceXY"));
			logger.debug("BufferSize: " + response.getBufferSize());
			Map<String, String[]> parameterMap = request.getParameterMap();
			Set<String> keySet = parameterMap.keySet();
			for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
				String key = iterator.next();
				logger.debug("> " + key + ": " + parameterMap.get(key)[0]);
			}
		}

		// BBOX=[longitude_west, latitude_south, longitude_east, latitude_north]
		String bboxParam = request.getParameter("BBOX");

		if ("Preview".equalsIgnoreCase(form_action)) {
			response.setContentType("text/plain;charset=UTF-8"); // Streaming
		} else { // "Download" y "NetworkLink"
			try {
				response.setContentType("application/vnd.google-earth.kml+xml;charset=UTF-8");
				response.setHeader("Content-Disposition", "attachment;filename=" + outFile);
			} catch (Exception e) {
				logger.error("Error configuring response" + e.getMessage(), e);
			}
		}

		ServletOutputStream outputStream = response.getOutputStream();
		encoder.setOutputStream(outputStream);
		producer.setBbox(bboxParam);
		producer.setQuery(query);
		producer.setProcesses(processes);

		try {
			URL url = Styles.class.getResource("/../../styles/" + stylesName);
			String sld = url.getFile();
			producer.setFeatureTypeStyleFromSLD(sld);
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Without sld archive");
			}
		}

		if ("NetworkLink".equalsIgnoreCase(form_action)) {
			String requestString = request.getQueryString().replace("NetworkLink", "Preview");
			requestString = request.getRequestURL() + "?" + requestString.replaceAll("&", "&amp;");
			try {
				producer.produceDocument(requestString);
			} catch (Exception e) {
				logger.error("Error producing document (NetworkLink)" + e.getMessage(), e);
				e.printStackTrace();
			}
		} else {
			try {
				producer.produceDocument();
			} catch (Exception e) {
				logger.error("Error producing document (Download/Preview)" + e.getMessage(), e);
				e.printStackTrace();
			}
		}
	}
}