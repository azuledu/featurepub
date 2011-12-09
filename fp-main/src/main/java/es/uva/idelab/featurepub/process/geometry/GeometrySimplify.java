package es.uva.idelab.featurepub.process.geometry;

import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

import es.uva.idelab.featurepub.process.AbstractProcess;
import es.uva.idelab.featurepub.process.Process;

public class GeometrySimplify extends AbstractProcess implements Process {

	final double defaultTolerance;

	public GeometrySimplify(double tolerance) {
		this.defaultTolerance = tolerance;
	}

	/**
	 * Douglas-Peucker algorithm.
	 * 
	 * @param feature
	 * @return
	 */
	public SimpleFeature processFeature(SimpleFeature feature)
	{
		Geometry geom = (Geometry) feature.getDefaultGeometry();
		geom = TopologyPreservingSimplifier.simplify(geom, defaultTolerance);
		feature.setDefaultGeometry(geom);
		return feature;
	}

}
