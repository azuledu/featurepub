package es.uva.idelab.featurepub.encoder.kml;

import java.io.IOException;

import org.geotools.kml.KML;
import org.geotools.kml.KMLConfiguration;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.xml.Encoder;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;

import es.uva.idelab.featurepub.encoder.AbstractEncoder;

public class KmlGtXmlEncoder extends AbstractEncoder implements KmlEncoder {

	private Encoder encoder = new Encoder(new KMLConfiguration());

	
	public KmlGtXmlEncoder() {
		System.setProperty("org.geotools.referencing.forceXY", "true");
		encoder.setIndenting(true);
		encoder.setOmitXMLDeclaration(true);
		encoder.setNamespaceAware(false);
	}
	
	public void startDocument(String thematicName) {
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
	
	public void putStyles(FeatureTypeStyle featureTypeStyle) {

	}

	
	public void putNetworklink(String requestString){
		try {
			outputStream.write("<NetworkLink>\n".getBytes());
			outputStream.write(("	<Link><href>" + requestString + "</href></Link>\n").getBytes());
			outputStream.write("</NetworkLink>".getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void putColladaObject(Geometry geometria, String scale) {
		// TODO
	}

	public void encodeFeature(SimpleFeature feature) {

		try {
			encoder.encode(feature, KML.Placemark, outputStream);
			outputStream.flush();
		} catch (Exception e) {
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
	
}
