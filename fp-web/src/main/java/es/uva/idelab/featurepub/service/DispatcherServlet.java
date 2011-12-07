package es.uva.idelab.featurepub.service;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
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
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import es.uva.idelab.featurepub.encoder.Encoder;
import es.uva.idelab.featurepub.encoder.kml.KmlDirectEncoder;
import es.uva.idelab.featurepub.process.Styles;
import es.uva.idelab.featurepub.producer.BasicProducer;
import es.uva.idelab.featurepub.producer.Producer;
import es.uva.idelab.featurepub.publisher.Publisher;

public class DispatcherServlet extends HttpServlet {

	private static final Log logger = LogFactory.getLog(DispatcherServlet.class);
	/**
	 * Root web app context
	 */
	WebApplicationContext appContext;
	/**
	 * ApplicationContext loaded from init directory
	 */
	private FileSystemXmlApplicationContext	configAppCtx;

	private Map<String,Publisher>	publishers=new Hashtable<String,Publisher>();
	private String	stylesName;
	
	public DispatcherServlet() {
		super();
	}
	
	public void init() {
		ServletContext servletContext = this.getServletContext();
		
		String prefix = servletContext.getRealPath("/");
// TODO substitute by commons logging
//		String file = getInitParameter("log4j-init-file");
//		// if the log4j-init-file is not set, then no point in trying
//		if (file != null) {
//			PropertyConfigurator.configure(prefix + file);
//		}
		
		reloadConf();
	}

	public void reloadConf() {
		
		String configPath = getInitParameter("configPath");
		String[] configPaths = new String[]{configPath};
		
		appContext =  WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		// Initialize the configuration
		if (this.configAppCtx==null)
			this.configAppCtx=new FileSystemXmlApplicationContext(configPaths,appContext);
		else
			configAppCtx.refresh();
		
		Map<String, Publisher> publishersMap=(Map<String, Publisher>) this.configAppCtx.getBean("publishersMap");
		this.publishers= publishersMap;
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String form_action = request.getParameter("form_action");
		if ("ReloadConf".equalsIgnoreCase(form_action)) {
			reloadConf();
			response.sendRedirect(response.encodeRedirectURL("/FeaturePub"));
			return;
		}

//		query = (Query) appContext.getBean(request.getParameter("queryName") + "Query");
//		this.processes = (List<Process>) appContext.getBean(request.getParameter("queryName") + "Process");
//		this.stylesName = request.getParameter("queryName") + ".sld";

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
		String publisherName=request.getParameter("queryName");
		String encoderName=request.getParameter("format"); // encoder
		
		
		
		/**
		 * Find queried publisher and encoder
		 */
		if (!publishers.containsKey(publisherName))
		{
			throw new IllegalArgumentException("Publisher not found:"+publisherName);
		}
		
		// creates a mutable map
		Map<String,Object> parameters= new HashMap<String,Object>(request.getParameterMap());
		// create reserved entries for BBOX and others
		BasicProducer.setBboxParams(bboxParam, parameters);
		
		Publisher publisher=this.publishers.get(publisherName);
		
		Encoder encoder=publisher.getEncoder(encoderName);
		Producer producer = publisher.getProducer();
		
		if ("Preview".equalsIgnoreCase(form_action)) {
			response.setContentType("text/plain;charset=UTF-8"); // Streaming
		} else 
		{ // "Download" y "NetworkLink"
				String outFile = "attachment;filename=" + getOutFile();
				String mimeType = encoder.getMimeType();
				response.setContentType(mimeType);
				response.setHeader("Content-Disposition", outFile);
		}

		ServletOutputStream outputStream = response.getOutputStream();
		
		encoder.setOutputStream(outputStream);

		try {
			URL url = Styles.class.getResource("/../../styles/" + stylesName);
			String sld = url.getFile();
			if (encoder instanceof KmlDirectEncoder)
				((KmlDirectEncoder)encoder).setFeatureTypeStyleFromSLD(sld);
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Without sld archive",e);
			}
		}

		
		if ("NetworkLink".equalsIgnoreCase(form_action)) 
		{
		/**
		 * generate a NetworkLink using the modified requestString
		 */
			String requestString = request.getQueryString().replace("NetworkLink", "Preview");
			requestString = request.getRequestURL() + "?" + requestString.replaceAll("&", "&amp;");
			parameters.put("RequestString", requestString);	
		}
		
		try {
			producer.produceDocument(
									response.getOutputStream(),
									publisher,
									parameters,
									encoder);
		} catch (Exception e) 
		{
			logger.error("Error producing document (NetworkLink)" + e.getMessage(), e);
		}
	}

	/**
	 * @return the stylesName
	 */
	public String getStylesName()
	{
		return stylesName;
	}

	/**
	 * @param stylesName the stylesName to set
	 */
	public void setStylesName(String stylesName)
	{
		this.stylesName=stylesName;
	}

	/**
	 * @param publishers the publishers to set
	 */
	public void setPublishers(Map<String, Publisher> publishers)
	{
		this.publishers=publishers;
	}

	private String getOutFile()
	{
		return "output.kml";
	}
}
