package es.uva.idelab.featurepub.Process;

import org.opengis.feature.simple.SimpleFeature;

import es.uva.idelab.featurepub.Process.Data.DataUtilities;

public class Styles implements Process {

	private String stylesName;

	public Styles(String stylesName) {
		this.stylesName = stylesName;
	}

	public SimpleFeature processFeature(SimpleFeature feature) {

		DataUtilities dataUtilities = new DataUtilities();
		feature = dataUtilities.addAttribute(feature, "styleUrl", stylesName);

		return feature;
	}

}
