package es.uva.idelab.featurepub.Process;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.opengis.feature.simple.SimpleFeature;

import es.uva.idelab.featurepub.DispatcherServlet;
import es.uva.idelab.featurepub.Process.Data.DataDAO;
import es.uva.idelab.featurepub.Process.Data.DataUtilities;
import es.uva.idelab.featurepub.Process.Data.PhotoData;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class Description implements Process {

	DataDAO dataDAO = null;
	List<DataDAO> dataDAOList = null;
	final String templateName;
	Template template;
	PhotoData photos;

	public PhotoData getPhotos() {
		return photos;
	}

	public void setPhotos(PhotoData photos) {
		this.photos = photos;
	}

	/**
	 * @param templateName
	 *            - Freemarker template file name.
	 */
	public Description(DataDAO dataDAO, String templateName) {
		this.dataDAO = dataDAO;
		this.templateName = templateName;

		Configuration freemarkerConfig = new Configuration();
		freemarkerConfig.setClassForTemplateLoading(DispatcherServlet.class, "../../../../../../templates");
		freemarkerConfig.setObjectWrapper(new DefaultObjectWrapper());

		try {
			this.template = freemarkerConfig.getTemplate(templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param templateName
	 *            - Freemarker template file name.
	 */
	public Description(List<DataDAO> dataDAOList, String templateName) {
		this.dataDAOList = dataDAOList;
		this.templateName = templateName;

		Configuration freemarkerConfig = new Configuration();
		freemarkerConfig.setClassForTemplateLoading(DispatcherServlet.class, "../../../../../../templates");
		freemarkerConfig.setObjectWrapper(new DefaultObjectWrapper());

		try {
			this.template = freemarkerConfig.getTemplate(templateName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add a "balloon" attached to the Feature.
	 * 
	 * @param feature
	 * @return
	 */
	public SimpleFeature processFeature(SimpleFeature feature) {

		StringBuffer description = new StringBuffer();
		Map<String, Object> dataMap = null;

		if (dataDAO != null) {
			dataMap = dataDAO.getDataMap(feature);
		} else {
			for (DataDAO dataDAOelement : dataDAOList) {
				dataMap = dataDAOelement.getDataMap(feature);
			}
		}

		String featureId = feature.getID().substring(feature.getID().lastIndexOf(".") + 1);
		dataMap.put("featureId", featureId);

		if (photos != null) {
			dataMap.put("photo", photos.getPreferredPhotoId(featureId));
		}

		Writer out = new StringWriter();
		try {
			template.process(dataMap, out);
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		description.append(out.toString());

		DataUtilities dataUtilities = new DataUtilities();
		feature = dataUtilities.addAttribute(feature, "description", description);

		return feature;
	}

}
