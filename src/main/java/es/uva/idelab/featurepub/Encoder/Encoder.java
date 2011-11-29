package es.uva.idelab.featurepub.Encoder;

import java.io.OutputStream;

import org.geotools.styling.FeatureTypeStyle;
import org.opengis.feature.simple.SimpleFeature;


public interface Encoder {
	
	public OutputStream getOutputStream();
	public void setOutputStream(OutputStream outputStream);
	
	public void startDocument(String thematicName);
	
	public void putStyles(FeatureTypeStyle featureTypeStyle);
		
	public void encodeFeature(SimpleFeature feature);

	public void endDocument();
	
}