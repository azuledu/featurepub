package es.uva.idelab.featurepub.process;

import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

public class GeometrySimplify implements Process {

	final double tolerance;

	public GeometrySimplify(double tolerance) {
		this.tolerance = tolerance;
	}

	/**
	 * Douglas-Peucker algorithm.
	 * 
	 * @param feature
	 * @return
	 */
	public SimpleFeature processFeature(SimpleFeature feature) {

		Geometry geom = (Geometry) feature.getDefaultGeometry();
		geom = TopologyPreservingSimplifier.simplify(geom, tolerance);
		feature.setDefaultGeometry(geom);
		return feature;
	}

}
