package es.uva.idelab.featurepub.producer;

import java.io.OutputStream;
import java.util.Map;

import es.uva.idelab.featurepub.encoder.Encoder;
import es.uva.idelab.featurepub.publisher.Publisher;

public interface ProducerContext
{
	public abstract Encoder getEncoder();

	public abstract Map<String, Object> getParameters();

	public abstract Publisher getPublisher();

	public abstract OutputStream getOutputStream();
}
