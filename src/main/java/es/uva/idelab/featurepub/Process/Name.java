package es.uva.idelab.featurepub.Process;

import es.uva.idelab.featurepub.Process.Data.DataUtilities;

import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;

public class Name implements Process {

	final String nameAttribute;

	public Name(String nameAttribute) {
		this.nameAttribute = nameAttribute;
	}

	/**
	 * Add a Name attached to the Feature.
	 * 
	 * @param feature
	 * @return
	 */
	@Override
	public SimpleFeature processFeature(SimpleFeature feature) {

		Property property = feature.getProperty(nameAttribute);
		String name = (String) property.getValue();

		DataUtilities dataUtilities = new DataUtilities();
		feature = dataUtilities.addAttribute(feature, "name", name);

		return feature;
	}

}
