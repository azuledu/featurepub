package es.uva.idelab.featurepub.ThematicEncoder;

import java.io.OutputStream;

import org.geotools.styling.FeatureTypeStyle;
import org.opengis.feature.simple.SimpleFeature;


public interface ThematicEncoder {
	
	public OutputStream getOutputStream();
	public void setOutputStream(OutputStream outputStream);
	
	public void startDocument(String thematicName);
	
	public void putStyles(FeatureTypeStyle featureTypeStyle);
		
	public void encodeFeature(SimpleFeature feature);

	public void endDocument();
	
}
