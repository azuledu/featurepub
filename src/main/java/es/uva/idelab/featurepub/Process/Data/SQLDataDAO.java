package es.uva.idelab.featurepub.Process.Data;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

public class SQLDataDAO implements DataDAO {

	private SimpleJdbcTemplate simpleJdbcTemplate;
	private String sql;
	private RowMapper<Object> rowMapper;
	private String featureFK;
	private Map<String, Object> dataMap;

	public SQLDataDAO(DataSource dataSource, String sql, RowMapper<Object> rowMapper, String featureFK) throws Exception {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
		this.sql = sql;
		this.rowMapper = rowMapper;
		this.featureFK = featureFK;
	}

	public Map<String, Object> getDataMap(SimpleFeature feature) {

		Property property = feature.getProperty(featureFK);
		Object propertyValue = property.getValue();
		if (propertyValue != null) {
			String featureFKValue = propertyValue.toString();
			this.dataMap = (Map<String, Object>) this.simpleJdbcTemplate.queryForObject(sql, rowMapper, featureFKValue);
		} else {
			this.dataMap = new HashMap<String, Object>();
		}

		return dataMap;
	}

}
