package es.uva.idelab.featurepub.producer;

import java.io.OutputStream;
import java.util.Map;

import es.uva.idelab.featurepub.encoder.Encoder;
import es.uva.idelab.featurepub.process.Process;
import es.uva.idelab.featurepub.publisher.Publisher;
/**
 * Bean to hold data about the current request for consumption of the Producer, {@link Encoder} and {@link Process}
 * @author juacas
 *
 */
public class SimpleProducerContext implements ProducerContext
{

	private OutputStream	outputStream;
	private Publisher	publisher;
	private Map<String, Object>	parameters;
	private Encoder	encoder;

	public SimpleProducerContext(OutputStream output, Publisher publisher2, Map<String, Object> parameters, Encoder encoder)
	{
		this.outputStream=output;
		this.publisher=publisher2;
		this.parameters=parameters;
		this.encoder=encoder;
	}

	/**
	 * @return the outputStream
	 */
	@Override
	public OutputStream getOutputStream()
	{
		return outputStream;
	}

	/**
	 * @return the publisher
	 */
	@Override
	public Publisher getPublisher()
	{
		return publisher;
	}

	/**
	 * @return the parameters
	 */
	@Override
	public Map<String, Object> getParameters()
	{
		return parameters;
	}

	/**
	 * @return the encoder
	 */
	@Override
	public Encoder getEncoder()
	{
		return encoder;
	}

}
