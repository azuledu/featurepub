package es.uva.idelab.featurepub;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.data.Query;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import es.uva.idelab.featurepub.Process.Styles;
import es.uva.idelab.featurepub.ThematicEncoder.ThematicEncoder;

public class DispatcherServlet extends HttpServlet {

	private static final Log logger = LogFactory.getLog(DispatcherServlet.class);
	ApplicationContext appContext;

	Query query;
	List<es.uva.idelab.featurepub.Process.Process> processes;
	String stylesName;
	ThematicEncoder thematicEncoder;
	Producer producer;

	private String outFile = "featurePub.kml";

	public DispatcherServlet() {
		super();
		appContext = new ClassPathXmlApplicationContext(new String[] { "applicationContext.xml", "featureFilters.xml",
				"featureQueries.xml", "connections.xml", "processes.xml", "data.xml" });
		this.producer = (Producer) appContext.getBean("producer");
		thematicEncoder = producer.getThematicEncoder();
	}

	public void setOutFile(String outFile) {
		this.outFile = outFile;
	}

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		this.query = (Query) appContext.getBean(request.getParameter("queryName") + "Query");
		this.processes = (List<es.uva.idelab.featurepub.Process.Process>) appContext.getBean(request.getParameter("queryName") + "Process");
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

		String form_action = request.getParameter("form_action");
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
		thematicEncoder.setOutputStream(outputStream);
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