package es.uva.idelab.featurepub.process;

import org.opengis.feature.simple.SimpleFeature;

public interface Process {

	public  static final String	ATT_STYLE_URL	="styleUrl";
	public  static final String	ATTR_NAME	="Name";
	
	public SimpleFeature processFeature(SimpleFeature feature);
	/**
	 * Provides a string representation suitable for making a unique cache key.
	 * i.e. it must include Process identification and relevant parameter values.
	 */
	public String getKeyString();
}
