package es.uva.idelab.featurepub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import junit.framework.TestCase;
import net.sf.ehcache.CacheManager;

import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.collection.CollectionDataStore;
import org.geotools.data.collection.CollectionFeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.junit.Test;

import es.uva.idelab.featurepub.encoder.Encoder;
import es.uva.idelab.featurepub.producer.BasicProducer;
import es.uva.idelab.featurepub.producer.Producer;
import es.uva.idelab.featurepub.publisher.BasicPublisher;

public class basicPublisherTest extends TestCase
{

private SimpleFeatureImpl	feat1;
private CollectionFeatureSource	featureSource;
private SimpleFeatureCollection	featureCollection;
private DataStore	store;
private String	expectedSimpleFeature=
	"Document:Feature Publish\r\n"+
	"SimpleFeatureImpl:Simple=[SimpleFeatureImpl.Attribute: param1<String id=TestFID>=string value 1, SimpleFeatureImpl.Attribute: param2<String id=TestFID>=string value 2, SimpleFeatureImpl.Attribute: param2<String id=TestFID>=string value 3, SimpleFeatureImpl.Attribute: GEOM<geometry id=TestFID>=POINT (40 -10)]\r\n"+
	"End Document.\r\n";
private String	expectedNoFeature=
"Document:Feature Publish\r\n"+
"End Document.\r\n";

/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		// TODO Auto-generated method stub
		super.setUp();
		/**
		 * Create sample FeatureSource	
		 */
		featureCollection=DataTestUtil.createTestFeatureCollection();
		featureSource=new CollectionFeatureSource(featureCollection);	
		store=createDataStore(featureCollection);
	}
	/**
 * @return
 */
private CollectionDataStore createDataStore(SimpleFeatureCollection featureCollection)
{
	return new CollectionDataStore(featureCollection);
}
@Test
public void testBasicUseCase() throws IOException
	{
	CacheManager cacheManager=new CacheManager();
	BasicProducer producer=new BasicProducer(cacheManager);
	Encoder encoder= createTestEncoder();
	Query query= new Query("Simple");
	
	
	BasicPublisher publisher= new BasicPublisher();
	publisher.setProducer(producer);
	publisher.putEncoder("Test", encoder);
	publisher.setDataStore(store);
	publisher.setQuery(query);
	
	Map<String, Object> params=new Hashtable<String, Object>();
	ByteArrayOutputStream output=new ByteArrayOutputStream();
	
	producer.produceDocument(output,publisher, params, encoder);
		
	assertEquals(expectedSimpleFeature, output.toString());
	}

@Test
public void testBBOXFilterUseCase() throws IOException
	{
	BasicProducer producer=new BasicProducer(new CacheManager());
	Encoder encoder= createTestEncoder();
	Query query= new Query("Simple");

	BasicPublisher publisher= new BasicPublisher();
	publisher.setProducer(producer);
	publisher.putEncoder("Test", encoder);
	publisher.setDataStore(store);
	publisher.setQuery(query);
	
	Map<String, Object> params=new Hashtable<String, Object>();
	params.put(Producer.PARAM_XMAX, 180.0d);
	params.put(Producer.PARAM_XMIN, -180.0d);
	params.put(Producer.PARAM_YMAX, 90.0d);
	params.put(Producer.PARAM_YMIN, -90.0d);
	
	ByteArrayOutputStream output=new ByteArrayOutputStream();
	producer.produceDocument(output,publisher, params, encoder);
	String result=output.toString();
	//String difs=StringUtils.difference(result, expectedSimpleFeature);
	
	assertEquals(expectedSimpleFeature, result);
	//Geometry-filtered feature
	output=new ByteArrayOutputStream();
	params.put(Producer.PARAM_XMAX, 180.0d);
	params.put(Producer.PARAM_XMIN, 41.0d);
	params.put(Producer.PARAM_YMAX, 90.0d);
	params.put(Producer.PARAM_YMIN, -90.0d);
	producer.produceDocument(output,publisher, params, encoder);
	result=output.toString();
	assertEquals(expectedNoFeature, result);
	}
/**
 * @return
 */
private Encoder createTestEncoder()
{
	return new DummyTestEncoder();	
}

}
