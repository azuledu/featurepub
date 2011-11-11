package es.uva.idelab.featurepub.Process.Thematic;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.dbcp.BasicDataSource;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import es.uva.idelab.featurepub.Process.Process;
import es.uva.idelab.featurepub.Process.Data.DataDAO;
import es.uva.idelab.featurepub.Process.Data.DataUtilities;

public class ChoroplethRanges implements Process {

	DataDAO dataDAO;
	BasicDataSource dataSource;

	String thematicAttribute;
	Number thematicAttributeValue;

	Double maxValue;
	Double minValue;
	
	String noDataColor;

	String opacity;
	
	HashMap<String, String> ranges;

	public ChoroplethRanges(DataDAO dataDAO, String thematicAttribute, String typeName, BasicDataSource dataSource, String noDataColor, String opacity, HashMap<String, String> ranges) {

		this.dataDAO = dataDAO;
		
		this.noDataColor = noDataColor;

		this.opacity = opacity;
		
		this.ranges = ranges;
		this.thematicAttribute = thematicAttribute;

		// Metodo para la consulta
		String sql = "SELECT max(" + thematicAttribute + "), min(" + thematicAttribute + ") FROM " + typeName;
		SimpleJdbcTemplate simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
		Map<String, Object> rangeMap = new HashMap<String, Object>(simpleJdbcTemplate.queryForMap(sql));

		this.minValue = Double.valueOf(rangeMap.get("MAX(" + thematicAttribute + ")").toString());
		this.maxValue = Double.valueOf(rangeMap.get("MIN(" + thematicAttribute + ")").toString());
	}

	public SimpleFeature processFeature(SimpleFeature feature) {

		thematicAttributeValue = (Number) dataDAO.getDataMap(feature).get(thematicAttribute);
		DataUtilities dataUtilities = new DataUtilities();
		
		if(thematicAttributeValue!=null){

			double thematicAttributePercent = (thematicAttributeValue.doubleValue() - minValue) / (maxValue - minValue);
			String styleUrl = null;
			for (Entry<String, String> range : ranges.entrySet()) {
				if (thematicAttributePercent <= Double.valueOf(range.getKey())) {
					styleUrl = range.getValue();
				}
			}
			feature = dataUtilities.addAttribute(feature, "styleUrl", styleUrl);	
		}

		else{
			feature = dataUtilities.addAttribute(feature, "color", noDataColor);
			feature = dataUtilities.addAttribute(feature, "opacity", opacity);
		}
		
		return feature;
	}

}
