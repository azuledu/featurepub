package es.uva.idelab.featurepub.producer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.opengis.filter.Filter;

import es.uva.idelab.featurepub.encoder.Encoder;
import es.uva.idelab.featurepub.process.Process;
import es.uva.idelab.featurepub.publisher.BasicPublisher;
import es.uva.idelab.featurepub.publisher.Publisher;
/**
 * Controller bean interface to execute and iterating algorithm through features
 * @author juacas
 *
 */
public interface Producer {

	static String	PARAM_XMIN	="Xmin";
	static String	PARAM_XMAX	="Xmax";
	static String	PARAM_YMIN	="Ymin";
	static String	PARAM_YMAX	="Ymax";
	/**
	 * {@link List} with {@link Filter} objects to be used in the query of features.
	 */
	static String	PARAM_FILTERLIST	="Ymax";
	
/**
 * Start the iteration over the features retrieved from the {@link Publisher} configuration.
 * Selected {@link Encoder} is used as a template for creating a new instance to attend this request.
 * Features are retrieved from the publisher's FeatureDataSource + Query
 * @param output	{@link OutputStream} in which dump the produced output to.
 * @param publisher {@link Publisher} bean with definition of the styling and formal details of the data to expose.
 * @param parameters {@link Map} with parameters retrieved from the original request of set by any {@link Process} in the process chain.  
 * @param encoderBase {@link Encoder} base instance to be cloned. Each new instance serves a single request in a single thread.
 * @throws IOException
 * @see {@link SimpleProducerContext}, {@link BasicProducer}, {@link BasicPublisher}
 */
	void produceDocument(OutputStream output, Publisher publisher, Map<String, Object> parameters, Encoder encoderBase) throws IOException;

}