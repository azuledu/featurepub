package es.uva.idelab.featurepub.process;

import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class Centroid implements Process {

	public SimpleFeature processFeature(SimpleFeature feature) {

		Geometry geom = (Geometry) feature.getDefaultGeometry();
		GeometryFactory geometryFactory = new GeometryFactory();
		Point point = geometryFactory.createPoint(new Coordinate(geom.getCentroid().getX(), geom.getCentroid().getY()));
		feature.setDefaultGeometry(point);

		return feature;
	}

}
