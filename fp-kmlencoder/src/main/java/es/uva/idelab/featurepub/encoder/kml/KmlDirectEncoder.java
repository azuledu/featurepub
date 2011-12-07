package es.uva.idelab.featurepub.encoder.kml;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.sld.v1_1.SLDConfiguration;
import org.geotools.styling.ExternalGraphicImpl;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Symbolizer;
import org.geotools.xml.Configuration;
import org.geotools.xml.Parser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.style.GraphicalSymbol;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import es.uva.idelab.featurepub.encoder.AbstractStyledEncoder;
import es.uva.idelab.featurepub.encoder.Encoder;

public class KmlDirectEncoder extends AbstractStyledEncoder implements KmlEncoder {
	private static Log logger = LogFactory.getLog(KmlDirectEncoder.class);
	private static final String OPEN_PLACEMARK = "\n<Placemark>";
	private static final String CLOSE_PLACEMARK = "</Placemark>";
	private static final String OPEN_MULTIGEOMETRY = "<MultiGeometry>";
	private static final String CLOSE_MULTIGEOMETRY = "</MultiGeometry>";	
	private static final String OPEN_LINESTRING = "<LineString>";
	private static final String CLOSE_LINESTRING = "</LineString>";
	private static final String OPEN_LINEARRING = "<LinearRing>";
	private static final String CLOSE_LINEARRING = "</LinearRing>";
	private static final String OPEN_POLYGON = "<Polygon>";
	private static final String CLOSE_POLYGON = "</Polygon>";
	private static final String OPEN_POINT = "<Point>";
	private static final String CLOSE_POINT = "</Point>";
	private static final String OPEN_NAME = "<name>";
	private static final String CLOSE_NAME = "</name>";
	private static final String OPEN_TESSELLATE = "<tessellate>";
	private static final String TESSELLATE_MODE = "1";
	private static final String CLOSE_TESSELLATE = "</tessellate>";	
	private static final String OPEN_ALTITUDE_MODE = "<altitudeMode>";
	private static final String ALTITUDE_MODE = "relativeToGround";
	private static final String CLOSE_ALTITUDE_MODE = "</altitudeMode>";
	private static final String OPEN_EXTRUDE = "<extrude>";
	private static final String CLOSE_EXTRUDE = "</extrude>";
	private static final String OPEN_DESCRIPTION = "<description><![CDATA[";
	private static final String CLOSE_DESCRIPTION = "]]></description>";
	private static final String OPEN_OUTERBOUNDARYIS = "<outerBoundaryIs>";
	private static final String CLOSE_OUTERBOUNDARYIS = "</outerBoundaryIs>";
	private static final String OPEN_STYLE = "<Style>";
	private static final String CLOSE_STYLE = "</Style>\n";
	private static final String OPEN_STYLEURL = "<styleUrl>#";
	private static final String CLOSE_STYLEURL = "</styleUrl>";
	private static final String OPEN_STYLEID = "\n<Style id=\"";
	private static final String CLOSE_STYLEID = "\">";
	private static final String OPEN_LINESTYLE = "<LineStyle>";
	private static final String OPEN_COLOUR = "<color>";
	private static final String CLOSE_COLOUR = "</color>";
	private static final String OPEN_LINEWIDTH = "<width>";
	private static final String CLOSE_LINEWIDTH = "</width>";
	private static final String CLOSE_LINESTYLE = "</LineStyle>";
	private static final String OPEN_POLYSTYLE = "<PolyStyle>";
	private static final String CLOSE_POLYSTYLE = "</PolyStyle>";
	private static final String OPEN_ICONSTYLE = "<IconStyle>";
	private static final String CLOSE_ICONSTYLE = "</IconStyle>";
	private static final String OPEN_ICON = "<Icon>";
	private static final String CLOSE_ICON = "</Icon>";
	private static final String OPEN_MODEL = "<Model>";
	private static final String CLOSE_MODEL = "</Model>";
	private static final String OPEN_LOCATION = "<Location>";
	private static final String CLOSE_LOCATION = "</Location>";
	private static final String OPEN_LONGITUDE = "<longitude>";
	private static final String CLOSE_LONGITUDE = "</longitude>";
	private static final String OPEN_LATITUDE = "<latitude>";
	private static final String CLOSE_LATITUDE = "</latitude>";
	private static final String OPEN_ALTITUDE = "<altitude>";
	private static final String CLOSE_ALTITUDE = "</altitude>";
	private static final String OPEN_SCALE = "<Scale>";
	private static final String CLOSE_SCALE = "</Scale>";
	private static final String OPEN_LINK = "<Link>";
	private static final String CLOSE_LINK = "</Link>";
	private static final String OPEN_HREF = "<href>";
	private static final String CLOSE_HREF = "</href>";
	private static final String OPEN_COORDINATES = "<coordinates>";
	private static final String CLOSE_COORDINATES = "</coordinates>";
	private static final String OPEN_X = "<x>";
	private static final String CLOSE_X = "</x>";
	private static final String OPEN_Y = "<y>";
	private static final String CLOSE_Y = "</y>";
	private static final String OPEN_Z = "<z>";
	private static final String CLOSE_Z = "</z>";
	
	
	FeatureTypeStyle featureTypeStyle;
	
	public KmlDirectEncoder() {}

	public void startDocument(String thematicName) {
		this.putStyles(featureTypeStyle);
		try {
			outputStream.write(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
							+ "<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n"
							+ "<Document>\n<name>").getBytes());
			outputStream.write(thematicName.getBytes());
			outputStream.write("</name>\n".getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void putNetworklink(String requestString) {
		try {
			outputStream.write("<NetworkLink>\n".getBytes());
			outputStream.write(("<refreshVisibility>0</refreshVisibility>\n" +
					"<flyToView>0</flyToView>\n").getBytes());
			outputStream.write(OPEN_LINK.getBytes());
			outputStream.write(OPEN_HREF.getBytes());
			outputStream.write(requestString.getBytes());			
			outputStream.write(CLOSE_HREF.getBytes());
			outputStream.write(("\n<refreshInterval>2</refreshInterval>\n" + "<viewRefreshMode>onStop</viewRefreshMode>\n" + "<viewRefreshTime>1</viewRefreshTime>\n").getBytes());
			outputStream.write(CLOSE_LINK.getBytes());
			outputStream.write("</NetworkLink>".getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void putStyles(FeatureTypeStyle featureTypeStyle) {
		
		if (featureTypeStyle != null) {
			for (Rule rule : featureTypeStyle.rules().toArray(new Rule[0])) {
				try {
					if (rule.getName().isEmpty()) {
						outputStream.write(OPEN_STYLE.getBytes());
					} else {
						outputStream.write(OPEN_STYLEID.getBytes());
						outputStream.write(rule.getName().getBytes());
						outputStream.write(CLOSE_STYLEID.getBytes());
					}

					for (Symbolizer symbolizer : rule.getSymbolizers()) {
						if (symbolizer.getClass().toString().contains("PolygonSymbolizer")) {
							PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) symbolizer;
							putFill(polygonSymbolizer.getFill().getOpacity().toString() , polygonSymbolizer.getFill().getColor().toString());
							putStroke(polygonSymbolizer.getStroke().getOpacity().toString() , polygonSymbolizer.getStroke().getColor().toString(), polygonSymbolizer.getStroke().getWidth().toString());
						}

						else if (symbolizer.getClass().toString().contains("LineSymbolizer")) {
							LineSymbolizer lineSymbolizer = (LineSymbolizer) symbolizer;
							putStroke(lineSymbolizer.getStroke().getOpacity().toString() , lineSymbolizer.getStroke().getColor().toString(), lineSymbolizer.getStroke().getWidth().toString());
						}

						else if (symbolizer.getClass().toString().contains("PointSymbolizer")) {
							PointSymbolizer pointSymbolizer = (PointSymbolizer) symbolizer;
							putGraphic(pointSymbolizer.getGraphic());
						}
					}

					outputStream.write(CLOSE_STYLE.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	public void putFill(String opacity, String color){	
		try {
			outputStream.write(OPEN_POLYSTYLE.getBytes());
			putKmlColor(opacity, color);
			outputStream.write(CLOSE_POLYSTYLE.getBytes());	

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void putStroke(String opacity, String color, String width) {
		try {
			// TODO incluir opacidad
			outputStream.write(OPEN_LINESTYLE.getBytes());
			putKmlColor(opacity, color);
			if (width != null) {
				outputStream.write(OPEN_LINEWIDTH.getBytes());
				outputStream.write(width.getBytes());
				outputStream.write(CLOSE_LINEWIDTH.getBytes());
			}
			outputStream.write(CLOSE_LINESTYLE.getBytes());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void putKmlColor(String opacity, String color){
		
		String red = color.substring(1,3);
		String green = color.substring(3,5);
		String blue = color.substring(5,7);	
		
		Double opacitydouble = Double.valueOf(opacity) * 255;
		Integer opacityInteger = opacitydouble.intValue();
		
		String opacityHex = Integer.toHexString(opacityInteger);
		
		while(opacityHex.length()<2){
			opacityHex = "0".concat(opacityHex);
		}		
		
			try {
				outputStream.write(OPEN_COLOUR.getBytes());
				outputStream.write((opacityHex + blue + green + red).getBytes() );
				outputStream.write(CLOSE_COLOUR.getBytes());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
	
	public void putGraphic(Graphic graphic){
		try {
			outputStream.write(OPEN_ICONSTYLE.getBytes());
			outputStream.write(OPEN_ICON.getBytes());
			outputStream.write(OPEN_HREF.getBytes());

			
			for(GraphicalSymbol graphicalSymbol : graphic.graphicalSymbols() ){
				if (graphicalSymbol.toString().contains("ExternalGraphicImpl")){
					ExternalGraphicImpl external = (ExternalGraphicImpl) graphicalSymbol;
					outputStream.write(external.getLocation().toString().getBytes());
				}
			}
			
			outputStream.write(CLOSE_HREF.getBytes());		
			outputStream.write(CLOSE_ICON.getBytes());
			outputStream.write(CLOSE_ICONSTYLE.getBytes());		

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void encodeFeature(SimpleFeature feature) {
		
		try {
			outputStream.write(OPEN_PLACEMARK.getBytes());
			
			if (feature.getProperty( "name" )!=null ) {
				putName(feature.getProperty("name").getValue().toString());
			}
			if (feature.getProperty( "styleUrl" )!=null ) {
				putStyleUrl(feature.getProperty( "styleUrl" ).getValue().toString());
			}
			if (feature.getProperty( "description" )!=null ) {
				putDescription(feature);
			}
			
			
			String geometryType = feature.getDefaultGeometry().toString();
			
			if (feature.getProperty( "color" )!=null && feature.getProperty( "opacity" )!=null) {
				outputStream.write(OPEN_STYLE.getBytes());
				if (geometryType.contains("POLYGON"))
					putFill(feature.getProperty( "opacity" ).getValue().toString(), feature.getProperty( "color" ).getValue().toString());
				putStroke(feature.getProperty( "opacity" ).getValue().toString(), feature.getProperty( "color" ).getValue().toString() , null);
				outputStream.write(CLOSE_STYLE.getBytes());
			}

			if(feature.getProperty( "collada" )!=null  ){
				outputStream.write(OPEN_MULTIGEOMETRY.getBytes());
				putColladaObject(feature);
			}
			
			if (geometryType.contains("POLYGON")) {
				putPolygon(feature);
			} else if (geometryType.contains("LINESTRING")) {
				putLineString(feature);
			} else if (geometryType.contains("POINT")) {
				putPoint(feature);
			}
			
			if(feature.getProperty( "collada" )!=null  ){
				outputStream.write(CLOSE_MULTIGEOMETRY.getBytes());				
			}			
			
			outputStream.write(CLOSE_PLACEMARK.getBytes());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private String generateCoordinates(SimpleFeature feature) {

		Geometry geometry = (Geometry) feature.getDefaultGeometry();
		StringBuffer coordinates = new StringBuffer();
		
		Coordinate[] coord = geometry.getCoordinates();
		for (Coordinate coordinate : coord){
			coordinates.append(coordinate.x).append(',').append(coordinate.y);
					
			if( !((Double)coordinate.z).isNaN() )
					coordinates.append(',').append(coordinate.z);
			
			coordinates.append(' ');
		}
		return coordinates.toString();
		
	}

	public void putLineString(SimpleFeature feature) {

		String coordenadas = generateCoordinates(feature);
		try {
			
			outputStream.write(("\n" + OPEN_LINESTRING).getBytes());
			outputStream.write(OPEN_COORDINATES.getBytes());
			outputStream.write(coordenadas.getBytes());
			outputStream.write(CLOSE_COORDINATES.getBytes());
			outputStream.write(CLOSE_LINESTRING.getBytes());
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void putPolygon(SimpleFeature feature) {
				
		try {

			outputStream.write(("\n" + OPEN_POLYGON).getBytes());

			// Diferencia entre prisma o no
			Geometry geometry = (Geometry) feature.getDefaultGeometry();
			Coordinate coordinate = geometry.getCoordinates()[0];

			if (!((Double) coordinate.z).isNaN()) {
				putAltitudeMode();
				putExtrude();
			} else {
				outputStream.write(OPEN_TESSELLATE.getBytes());
				outputStream.write(TESSELLATE_MODE.getBytes());
				outputStream.write(CLOSE_TESSELLATE.getBytes());
			}
			
			String coordenadas = generateCoordinates(feature);

			outputStream.write(OPEN_OUTERBOUNDARYIS.getBytes());
			outputStream.write(OPEN_LINEARRING.getBytes());
			outputStream.write(OPEN_COORDINATES.getBytes());
			outputStream.write(coordenadas.getBytes());
			outputStream.write(CLOSE_COORDINATES.getBytes());
			outputStream.write(CLOSE_LINEARRING.getBytes());
			outputStream.write(CLOSE_OUTERBOUNDARYIS.getBytes());
			outputStream.write(CLOSE_POLYGON.getBytes());

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
		
	public void putPoint(SimpleFeature feature) {

		String coordenadas = generateCoordinates(feature);
			
		try {

			outputStream.write(("\n" + OPEN_POINT).getBytes());
			outputStream.write(OPEN_COORDINATES.getBytes());
			outputStream.write(coordenadas.getBytes());
			outputStream.write(CLOSE_COORDINATES.getBytes());

			outputStream.write(CLOSE_POINT.getBytes());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void putColladaObject(SimpleFeature feature) {
		try {
			// if (logger.isDebugEnabled())
			// logger.debug(feature.getAttribute("description").getClass().toString());

			HashMap<String, Object> collada = (HashMap<String, Object>) feature.getAttribute("collada");

			if (collada != null) {
				outputStream.write(("\n" + OPEN_MODEL).getBytes());

				outputStream.write(OPEN_LINK.getBytes());
				outputStream.write(OPEN_HREF.getBytes());
				outputStream.write((collada.get("colladaLink")).toString().getBytes());
				outputStream.write(CLOSE_HREF.getBytes());
				outputStream.write(CLOSE_LINK.getBytes());

				outputStream.write(OPEN_ALTITUDE_MODE.getBytes());
				outputStream.write("clampToGround".getBytes());
				outputStream.write(CLOSE_ALTITUDE_MODE.getBytes());
				outputStream.write(OPEN_LOCATION.getBytes());

				// No tienen en ningún caso coordZ
				outputStream.write(OPEN_LONGITUDE.getBytes());
				outputStream.write((collada.get("coordX")).toString().getBytes());
				outputStream.write(CLOSE_LONGITUDE.getBytes());
				outputStream.write(OPEN_LATITUDE.getBytes());
				outputStream.write((collada.get("coordY")).toString().getBytes());
				outputStream.write(CLOSE_LATITUDE.getBytes());

				outputStream.write(CLOSE_LOCATION.getBytes());

				outputStream.write(OPEN_SCALE.getBytes());
				outputStream.write(OPEN_X.getBytes());
				outputStream.write((collada.get("scaleX")).toString().getBytes());
				outputStream.write(CLOSE_X.getBytes());

				outputStream.write(OPEN_Y.getBytes());
				outputStream.write((collada.get("scaleY")).toString().getBytes());
				outputStream.write(CLOSE_Y.getBytes());

				outputStream.write(OPEN_Z.getBytes());
				outputStream.write((collada.get("scaleZ")).toString().getBytes());
				outputStream.write(CLOSE_Z.getBytes());

				outputStream.write(CLOSE_SCALE.getBytes());
				outputStream.write(CLOSE_MODEL.getBytes());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void putName(String name){
		
		try {
			outputStream.write(OPEN_NAME.getBytes());
			outputStream.write(name.getBytes());
			outputStream.write(CLOSE_NAME.getBytes());			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void putStyleUrl(String styleUrl) {
		try {
			outputStream.write(OPEN_STYLEURL.getBytes());
			outputStream.write(styleUrl.getBytes());
			outputStream.write(CLOSE_STYLEURL.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void putDescription(SimpleFeature feature) {
		
		try {
			outputStream.write(OPEN_DESCRIPTION.getBytes());
			outputStream.write(feature.getAttribute("description").toString().getBytes());
			outputStream.write(CLOSE_DESCRIPTION.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	void putExtrude() {
		try {
			outputStream.write(OPEN_EXTRUDE.getBytes());
			outputStream.write("1".getBytes());
			outputStream.write(CLOSE_EXTRUDE.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void putAltitudeMode() {
		try {
			outputStream.write(OPEN_ALTITUDE_MODE.getBytes());
			outputStream.write(ALTITUDE_MODE.getBytes());
			outputStream.write(CLOSE_ALTITUDE_MODE.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void endDocument() {
		try {
			outputStream.write("\n</Document>\n</kml>".getBytes());
			outputStream.close(); // flush and close
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getMimeType() {
		return "application/vnd.google-earth.kml+xml;charset=UTF-8";
	}
	/**
	 * 
	 * @param sld
	 */
	public void setFeatureTypeStyleFromSLD(String sld) {
		try {
			FileReader fileReader = new FileReader(sld);
			Configuration config = new SLDConfiguration();
			Parser parser = new Parser(config);
			FeatureTypeStyle featureTypeStyle = (FeatureTypeStyle) parser.parse(fileReader);
			this.featureTypeStyle = featureTypeStyle;

		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Parse failed");
			}
		}
	}

	@Override
	public Encoder clone()
	{
		return this.clone();
	}
}