package es.uva.idelab.featurepub.process.geometry;

import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import es.uva.idelab.featurepub.process.AbstractProcess;
import es.uva.idelab.featurepub.process.Process;

public class Centroid extends AbstractProcess implements Process {

	@Override
	public SimpleFeature processFeature(SimpleFeature feature) {

		Geometry geom = (Geometry) feature.getDefaultGeometry();
		Point point = computeCentroid(geom);
		feature.setDefaultGeometry(point);

		return feature;
	}

	/**
	 * @param geom
	 * @param geometryFactory
	 * @return {@link Point} with the centroid
	 */
	private Point computeCentroid2(Geometry geom)
	{
		GeometryFactory geometryFactory = new GeometryFactory();
		return geometryFactory.createPoint(new Coordinate(geom.getCentroid().getX(), geom.getCentroid().getY()));
	}
	private Point computeCentroid(Geometry geom)
	{
		return geom.getCentroid();
	}
}
