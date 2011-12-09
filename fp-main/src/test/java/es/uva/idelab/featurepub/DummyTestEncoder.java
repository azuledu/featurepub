package es.uva.idelab.featurepub;

import java.io.OutputStream;
import java.io.PrintStream;

import org.geotools.styling.FeatureTypeStyle;
import org.opengis.feature.simple.SimpleFeature;

import es.uva.idelab.featurepub.encoder.Encoder;

public final class DummyTestEncoder implements Encoder
{

/**
 * 
 */
DummyTestEncoder()
{
	
}

	private OutputStream	stream;
	private PrintStream	dataStream;

	@Override
	public OutputStream getOutputStream()
	{
		return stream;
	}

	@Override
	public void setOutputStream(OutputStream outputStream)
	{
		this.stream=outputStream;
		this.dataStream=new PrintStream(outputStream);
	}

	@Override
	public void startDocument(String thematicName)
	{
		dataStream.println("Document:"+thematicName);			
	}

	@Override
	public void putStyles(FeatureTypeStyle featureTypeStyle)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void encodeFeature(SimpleFeature feature)
	{
		dataStream.println(feature);			
	}

	@Override
	public void endDocument()
	{
		dataStream.println("End Document.");
	}

	@Override
	public String getMimeType()
	{
		return "text/plain";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Encoder clone() throws CloneNotSupportedException
	{
		// TODO Auto-generated method stub
		return (Encoder) super.clone();
	}
}