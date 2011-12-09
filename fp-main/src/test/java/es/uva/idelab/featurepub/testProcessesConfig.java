package es.uva.idelab.featurepub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import es.uva.idelab.featurepub.encoder.Encoder;
import es.uva.idelab.featurepub.process.data.FieldCopy;
import es.uva.idelab.featurepub.publisher.Publisher;

public class testProcessesConfig
{

	/**
	 * Test in the producer uses a {@link FieldCopy} from attribute name to placemark
	 * @throws IOException
	 */
	@Test
	public void testFieldCopy() throws IOException
	{
		ApplicationContext ctxt=new GenericXmlApplicationContext("fp-config.xml");
		FeaturePublisherConfiguration config= (FeaturePublisherConfiguration) ctxt.getBean("config");
		Publisher publisher=config.getAvailablePublishers().get("testPublisher2");
		
		Encoder encoder=publisher.getEncoder("Dummy");
		
		ByteArrayOutputStream output=new ByteArrayOutputStream();

		publisher.getProducer().produceDocument(output, publisher,Collections.EMPTY_MAP, encoder);
		
		String out=output.toString();
		Assert.assertTrue(out.contains("Attribute: placemark"));
	}
}
