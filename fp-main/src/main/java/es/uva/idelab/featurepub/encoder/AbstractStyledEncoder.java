package es.uva.idelab.featurepub.encoder;

import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.styling.FeatureTypeStyle;
import org.opengis.feature.simple.SimpleFeature;

public abstract class AbstractStyledEncoder implements Encoder
{

	final Log logger = LogFactory.getLog(this.getClass());
	
	protected OutputStream outputStream;

	
	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	@Override
	public abstract void startDocument(String thematicName);

	@Override
	public abstract void putStyles(FeatureTypeStyle featureTypeStyle);

	@Override
	public abstract void encodeFeature(SimpleFeature feature);

	@Override
	public abstract void endDocument();

	@Override
	public abstract String getMimeType();
	public abstract Encoder clone();
}
