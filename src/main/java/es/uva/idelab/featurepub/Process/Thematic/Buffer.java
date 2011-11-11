package es.uva.idelab.featurepub.Process.Thematic;

import org.opengis.feature.simple.SimpleFeature;
import com.vividsolutions.jts.geom.Geometry;

import es.uva.idelab.featurepub.Process.Process;
import es.uva.idelab.featurepub.Process.Data.DataDAO;

public class Buffer implements Process {

	DataDAO dataDAO;
	final String thematicAttribute;
	final double thematicAttributeValueProportion;
	Number thematicAttributeValue;

	public Buffer(DataDAO dataDAO, String thematicAttribute, double thematicAttributeValueProportion) {
		this.dataDAO = dataDAO;
		this.thematicAttribute = thematicAttribute;
		this.thematicAttributeValueProportion = thematicAttributeValueProportion;
	}

	public SimpleFeature processFeature(SimpleFeature feature) {

		thematicAttributeValue = (Number) dataDAO.getDataMap(feature).get(thematicAttribute);
		if((thematicAttributeValue!=null) && (thematicAttributeValue.doubleValue()!=0)){
			Geometry geom = (Geometry) feature.getDefaultGeometry();
			geom = geom.getCentroid().buffer(thematicAttributeValueProportion * thematicAttributeValue.doubleValue());
			feature.setDefaultGeometry(geom);			
		}
		return feature;
	}

}