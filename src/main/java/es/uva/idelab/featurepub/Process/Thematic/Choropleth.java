package es.uva.idelab.featurepub.Process.Thematic;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;

import org.opengis.feature.simple.SimpleFeature;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import es.uva.idelab.featurepub.Process.Process;
import es.uva.idelab.featurepub.Process.Data.DataDAO;
import es.uva.idelab.featurepub.Process.Data.DataUtilities;

public class Choropleth implements Process {

	DataDAO dataDAO;
	BasicDataSource dataSource;

	String thematicAttribute;
	Number thematicAttributeValue;

	Double maxValue;
	Double minValue;

	Integer redmin;
	Integer greenmin;
	Integer bluemin;

	Integer redmax;
	Integer greenmax;
	Integer bluemax;

	String noDataColor;
	
	String opacity;

	public Choropleth(DataDAO dataDAO, String thematicAttribute, String typeName, BasicDataSource dataSource, String maxColor, String minColor,
			String noDataColor, String opacity) {

		this.dataDAO = dataDAO;
		this.thematicAttribute = thematicAttribute;
		this.dataSource = dataSource;

		// Metodo para la consulta
		String sql = "SELECT max(" + thematicAttribute + "), min(" + thematicAttribute + ") FROM " + typeName;
		SimpleJdbcTemplate simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
		Map<String, Object> rangeMap = new HashMap<String, Object>(simpleJdbcTemplate.queryForMap(sql));

		this.minValue = Double.valueOf(rangeMap.get("MAX(" + thematicAttribute + ")").toString());
		this.maxValue = Double.valueOf(rangeMap.get("MIN(" + thematicAttribute + ")").toString());

		this.redmax = Integer.parseInt(maxColor.substring(1, 3), 16);
		this.greenmax = Integer.parseInt(maxColor.substring(3, 5), 16);
		this.bluemax = Integer.parseInt(maxColor.substring(5, 7), 16);

		this.redmin = Integer.parseInt(minColor.substring(1, 3), 16);
		this.greenmin = Integer.parseInt(minColor.substring(3, 5), 16);
		this.bluemin = Integer.parseInt(minColor.substring(5, 7), 16);

		this.noDataColor = noDataColor;

		this.opacity = opacity;
	}

	public SimpleFeature processFeature(SimpleFeature feature) {

		thematicAttributeValue = (Number) dataDAO.getDataMap(feature).get(thematicAttribute);
		String color = noDataColor;
		
		if (thematicAttributeValue != null) {
			
			double thematicAttributePercent = (thematicAttributeValue.doubleValue() - minValue) / (maxValue - minValue);

			Double red = redmin + ((redmax - redmin) * thematicAttributePercent);
			Double green = greenmin + ((greenmax - greenmin) * thematicAttributePercent);
			Double blue = bluemin + ((bluemax - bluemin) * thematicAttributePercent);
			
			color = "#" + setHexColor(red) + setHexColor(green) + setHexColor(blue);
		}

		DataUtilities dataUtilities = new DataUtilities();
		feature = dataUtilities.addAttribute(feature, "color", color);
		feature = dataUtilities.addAttribute(feature, "opacity", opacity);

		return feature;
	}

	public String setHexColor(Double color) {

		String stringColor = Integer.toHexString(color.intValue());

		while (stringColor.length() < 2) {
			stringColor = "0".concat(stringColor);
		}

		return stringColor;
	}

	/*
	 * public DateRange setDateRange(String thematicAttribute, String typeName)
	 * {
	 * 
	 * String sql = "SELECT max(" + thematicAttribute + "), min(" +
	 * thematicAttribute + ") FROM " + typeName; Map<String, Object> rangeMap =
	 * new HashMap<String, Object>(getSimpleJdbcTemplate().queryForMap(sql));
	 * 
	 * Date minValue = (Date)rangeMap.get("MAX(" + thematicAttribute + ")");
	 * Date maxValue = (Date)rangeMap.get("MIN(" + thematicAttribute + ")");
	 * 
	 * return new DateRange(minValue, maxValue); }
	 */

}
