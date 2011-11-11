package es.uva.idelab.featurepub.Process.Thematic;

import java.util.HashMap;
import java.util.Map;

import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;

import es.uva.idelab.featurepub.Process.Process;
import es.uva.idelab.featurepub.Process.Data.DataDAO;
import es.uva.idelab.featurepub.Process.Data.DataUtilities;

public class Collada implements Process {

	DataDAO dataDAO;
	final String thematicAttribute;
	final double thematicScaleX;
	final double thematicScaleY;
	final double thematicScaleZ;

	final String colladaLink;
	Number thematicAttributeValue;

	Map<String, Object> collada = new HashMap<String, Object>();

	public Collada(DataDAO dataDAO, String thematicAttribute, double thematicScaleX, double thematicScaleY, double thematicScaleZ,
			String colladaLink) {

		this.dataDAO = dataDAO;
		this.thematicAttribute = thematicAttribute;
		this.thematicScaleX = thematicScaleX;
		this.thematicScaleY = thematicScaleY;
		this.thematicScaleZ = thematicScaleZ;

		this.colladaLink = colladaLink;
	}

	public SimpleFeature processFeature(SimpleFeature feature) {

		if (thematicAttributeValue != null) {

			thematicAttributeValue = (Number) dataDAO.getDataMap(feature).get(thematicAttribute);
			// Obtención de centroide
			Geometry geom = (Geometry) feature.getDefaultGeometry();
			double coordX = geom.getInteriorPoint().getX();
			double coordY = geom.getInteriorPoint().getY();

			// Inclusión en map de las coordenadas
			collada.put("coordX", coordX);
			collada.put("coordY", coordY);

			// Inclusión en map de la collada
			collada.put("colladaLink", colladaLink);

			// Obtención de escala
			putScales();

			// Inclusión de map en feature
			DataUtilities dataUtilities = new DataUtilities();
			feature = dataUtilities.addAttribute(feature, "collada", collada);
		}
		return feature;
	}

	public void putScales() {

		collada.put("scaleX", Math.log(thematicScaleX * thematicAttributeValue.doubleValue()));
		collada.put("scaleY", Math.log(thematicScaleY * thematicAttributeValue.doubleValue()));
		collada.put("scaleZ", Math.log(thematicScaleZ * thematicAttributeValue.doubleValue()));
	}

}
