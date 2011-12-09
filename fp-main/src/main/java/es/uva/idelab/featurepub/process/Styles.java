package es.uva.idelab.featurepub.process;

import org.opengis.feature.simple.SimpleFeature;

import es.uva.idelab.featurepub.process.data.DataUtilities;
/**
 * Sets a style name in the feature
 * @author juacas
 *
 */
public class Styles extends AbstractProcess implements Process {

	private String stylesName;

	public Styles(String stylesName) {
		this.stylesName = stylesName;
	}

	public SimpleFeature processFeature(SimpleFeature feature) {

		DataUtilities dataUtilities = new DataUtilities();
		feature = dataUtilities.addAttribute(feature, ATT_STYLE_URL, stylesName);

		return feature;
	}

}
