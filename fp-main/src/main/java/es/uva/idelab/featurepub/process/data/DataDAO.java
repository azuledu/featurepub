package es.uva.idelab.featurepub.process.data;

import java.util.Map;

import org.opengis.feature.simple.SimpleFeature;

public interface DataDAO {

	public Map<String, Object> getDataMap(SimpleFeature feature);

}
