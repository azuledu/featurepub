package es.uva.idelab.featurepub.process.data;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import org.springframework.jdbc.core.RowMapper;

public class DBRowMapper implements RowMapper<Object> {

	public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
		Map<String, Object> row = new HashMap<String, Object>();
		String colName;
		Object colValue;

		ResultSetMetaData metadata = rs.getMetaData();
		int columnCount = metadata.getColumnCount();
		for (int columnIndex = 1; columnIndex < (columnCount + 1); columnIndex++) {
			colName = metadata.getColumnName(columnIndex);
			colValue = rs.getObject(colName);
			row.put(colName, colValue);
		}
		return row;
	}
}