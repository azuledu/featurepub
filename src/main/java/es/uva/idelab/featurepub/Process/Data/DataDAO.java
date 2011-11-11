package es.uva.idelab.featurepub.Process.Data;

import java.util.Map;

import org.opengis.feature.simple.SimpleFeature;

public interface DataDAO {

	public Map<String, Object> getDataMap(SimpleFeature feature);

}
