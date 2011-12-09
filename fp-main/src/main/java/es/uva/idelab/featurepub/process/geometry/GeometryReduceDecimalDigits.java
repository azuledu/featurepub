package es.uva.idelab.featurepub.process.geometry;

import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.precision.GeometryPrecisionReducer;

import es.uva.idelab.featurepub.process.AbstractProcess;
import es.uva.idelab.featurepub.process.Process;

public class GeometryReduceDecimalDigits extends AbstractProcess implements Process {

	final int numDecDigits;

	public GeometryReduceDecimalDigits(int numDecDigits) {
		this.numDecDigits = numDecDigits;
	}

	/**
	 * Reduce the number of decimal digits in the coordinates of a geometry.
	 * 
	 * @param feature
	 * @return
	 */
	public SimpleFeature processFeature(SimpleFeature feature) {

		// TODO
		// http://docs.geotools.org/latest/userguide/guide/library/data/pregeneralized.html

		Geometry geom = (Geometry) feature.getDefaultGeometry();
		double scale = Math.pow(10, numDecDigits);
		PrecisionModel pm = new PrecisionModel(scale);
		geom = GeometryPrecisionReducer.reduce(geom, pm);

		/*
		 * if ((geom.isEmpty()) || (!geom.isValid()) || geom == null) {
		 * continue;// ignore this element and process the next one } else {
		 * feature.setDefaultGeometry(geom); }
		 */

		feature.setDefaultGeometry(geom);
		return feature;
	}

}
