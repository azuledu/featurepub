package es.uva.idelab.featurepub;

import java.io.IOException;
import java.util.Collections;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import es.uva.idelab.featurepub.encoder.Encoder;
import es.uva.idelab.featurepub.publisher.Publisher;

public class testSpringConfig
{

	@Test
	public void testContextWiring()
	{
		ApplicationContext ctxt=new GenericXmlApplicationContext("fp-config.xml");
		FeaturePublisherConfiguration config= (FeaturePublisherConfiguration) ctxt.getBean("config");
		Assert.assertFalse(config.getServerName().isEmpty());
		Assert.assertTrue(config.getAvailablePublishers().containsKey("testPublisher1"));
		Publisher testPublisher=config.getAvailablePublishers().get("testPublisher1");
		Encoder encoder=testPublisher.getEncoder("Test");
		Assert.assertNotNull(encoder);
	}
	@Test
	public void testContextProcessing() throws IOException
	{
		ApplicationContext ctxt=new GenericXmlApplicationContext("fp-config.xml");
		FeaturePublisherConfiguration config= (FeaturePublisherConfiguration) ctxt.getBean("config");
		Publisher publisher=config.getAvailablePublishers().get("testPublisher1");
		
		Encoder encoder=publisher.getEncoder("Dummy");
		publisher.getProducer().produceDocument(System.out, publisher,Collections.EMPTY_MAP, encoder);
	}
	@Test
	public void testContextProcessing2() throws IOException
	{
		ApplicationContext ctxt=new GenericXmlApplicationContext("fp-config.xml");
		FeaturePublisherConfiguration config= (FeaturePublisherConfiguration) ctxt.getBean("config");
		Publisher publisher=config.getAvailablePublishers().get("testPublisher2");
		
		Encoder encoder=publisher.getEncoder("Dummy");
		publisher.getProducer().produceDocument(System.out, publisher,Collections.EMPTY_MAP, encoder);
	}
}
