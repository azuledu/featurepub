package es.uva.idelab.featurepub.process;

import org.opengis.feature.simple.SimpleFeature;

public interface Process {

	public SimpleFeature processFeature(SimpleFeature feature);

}