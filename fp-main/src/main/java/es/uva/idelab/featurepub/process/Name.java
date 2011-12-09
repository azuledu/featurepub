package es.uva.idelab.featurepub.process;

import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;

import es.uva.idelab.featurepub.process.data.DataUtilities;

public class Name extends AbstractProcess implements Process {

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

		feature = DataUtilities.addAttribute(feature, Process.ATTR_NAME, name);

		return feature;
	}

}
