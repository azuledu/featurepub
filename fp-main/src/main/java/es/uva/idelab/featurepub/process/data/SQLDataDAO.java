package es.uva.idelab.featurepub.process.data;

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

	public SQLDataDAO(DataSource dataSource) throws Exception {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public void setRowMapper(RowMapper<Object> rowMapper) {
		this.rowMapper = rowMapper;
	}

	public void setFeatureFK(String featureFK) {
		this.featureFK = featureFK;
	}

	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
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
