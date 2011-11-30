package es.uva.idelab.featurepub.producer;

import java.util.Map;

import es.uva.idelab.featurepub.Publisher;

public interface producer {

	void produceDocument(Publisher publisher, Map<String, Object> parameters);

}