package es.uva.idelab.featurepub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import net.sf.ehcache.CacheManager;

import org.apache.commons.lang3.StringUtils;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.collection.CollectionDataStore;
import org.geotools.data.collection.CollectionFeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.feature.type.FeatureTypeFactoryImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.util.SimpleInternationalString;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureTypeFactory;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.identity.FeatureId;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

import es.uva.idelab.featurepub.encoder.Encoder;
import es.uva.idelab.featurepub.producer.BasicProducer;
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
		FeatureId fid=new FeatureIdImpl("TestFID");
		SimpleFeatureType featureType=createFeatureType();
		
		List<Object> atrs=new ArrayList<Object>();
		atrs.add("string value 1");
		atrs.add("string value 2");
		atrs.add("string value 3");
		//GeometryBuilder factory=new GeometryBuilder(DefaultGeographicCRS.WGS84);	
		GeometryFactory geomFact=new GeometryFactory();
		CoordinateSequence coords=new CoordinateArraySequence(new Coordinate[]{new Coordinate(40,-10)});
		Point pointJTS=new Point(coords,geomFact);
		atrs.add(pointJTS);// Geometry
		
		this.feat1=new SimpleFeatureImpl(atrs, featureType, fid);
		featureCollection=new DefaultFeatureCollection("TestCollection",featureType);
		featureCollection.add(feat1);
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
	BasicProducer producer=new BasicProducer(new CacheManager());
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
	params.put(Producer.XMAX, 180.0d);
	params.put(Producer.XMIN, -180.0d);
	params.put(Producer.YMAX, 90.0d);
	params.put(Producer.YMIN, -90.0d);
	
	ByteArrayOutputStream output=new ByteArrayOutputStream();
	producer.produceDocument(output,publisher, params, encoder);
	String result=output.toString();
	String difs=StringUtils.difference(result, expectedSimpleFeature);
	
	assertEquals(expectedSimpleFeature, result);
	//Geometry-filtered feature
	output=new ByteArrayOutputStream();
	params.put(Producer.XMAX, 180.0d);
	params.put(Producer.XMIN, 41.0d);
	params.put(Producer.YMAX, 90.0d);
	params.put(Producer.YMIN, -90.0d);
	producer.produceDocument(output,publisher, params, encoder);
	result=output.toString();
	assertEquals(expectedNoFeature, result);
	}
/**
 * @return
 */
private Encoder createTestEncoder()
{
	return new Encoder(){

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
	};
		
}

/**
 * @return
 */
private SimpleFeatureType createFeatureType()
{
	FeatureTypeFactory factory=new FeatureTypeFactoryImpl();
	AttributeType type=factory.createAttributeType(new NameImpl("String"), String.class, true, false, null, null, new SimpleInternationalString("Sample String"));
	
	List<AttributeDescriptor> schema=new ArrayList<AttributeDescriptor>();
	schema.add(factory.createAttributeDescriptor(type, new NameImpl("param1"), 1,1, true, null));
	schema.add(factory.createAttributeDescriptor(type, new NameImpl("param2"), 1,1, true, null));
	schema.add(factory.createAttributeDescriptor(type, new NameImpl("param2"), 1,1, true, null));
	//GeometryType geotype=factory.createGeometryType(new NameImpl("geometry"), DirectPosition.class, DefaultGeographicCRS.WGS84, true, false, null, null, new SimpleInternationalString("Geometry test def"));
	GeometryType geotype=factory.createGeometryType(new NameImpl("geometry"), Geometry.class, DefaultGeographicCRS.WGS84, true, false, null, null, new SimpleInternationalString("Geometry test def"));

	GeometryDescriptor geomdesc=factory.createGeometryDescriptor(geotype, new NameImpl("GEOM"), 1, 1, true, null);
	schema.add(geomdesc);
	SimpleFeatureType featureType=factory.createSimpleFeatureType(new NameImpl("Simple"), schema, geomdesc, false, null, null, new SimpleInternationalString("TestFeature"));
	return featureType;
}

}
