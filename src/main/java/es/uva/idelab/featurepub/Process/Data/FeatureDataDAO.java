package es.uva.idelab.featurepub.Process.Data;

import java.util.HashMap;
import java.util.Map;

import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;

public class FeatureDataDAO implements DataDAO {

	@Override
	public Map<String, Object> getDataMap(SimpleFeature feature) {

		Map<String, Object> dataMap = new HashMap<String, Object>();

		for (Property property : feature.getProperties()) {
			String name = property.getName().getLocalPart();
			Object value = feature.getAttribute(name);
			dataMap.put(name, value);
		}

		return dataMap;
	}
}
