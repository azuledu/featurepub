package es.uva.idelab.featurepub.process.thematic;

import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Geometry;

import es.uva.idelab.featurepub.process.AbstractProcess;
import es.uva.idelab.featurepub.process.Process;
import es.uva.idelab.featurepub.process.data.DataDAO;

public class Altitude extends AbstractProcess implements Process {

	DataDAO dataDAO;
	final String thematicAttribute;
	final double thematicAttributeValueProportion;
	Number thematicAttributeValue;

	
	public Altitude(DataDAO dataDAO, String thematicAttribute, double thematicAttributeValueProportion) {
		this.dataDAO = dataDAO;
		this.thematicAttribute = thematicAttribute;
		this.thematicAttributeValueProportion = thematicAttributeValueProportion;
	}

	public SimpleFeature processFeature(SimpleFeature feature) {

		thematicAttributeValue = (Number) dataDAO.getDataMap(feature).get(thematicAttribute);
		if (thematicAttributeValue != null) {
			Geometry geom = (Geometry) feature.getDefaultGeometry();
			geom.apply(new CoordinateFilter() {
				public void filter(Coordinate coord) {
					coord.z = thematicAttributeValueProportion * thematicAttributeValue.doubleValue();
				}
			});
		}

		return feature;
	}

}