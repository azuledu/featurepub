package es.uva.idelab.featurepub;

import java.util.ArrayList;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.feature.type.FeatureTypeFactoryImpl;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.util.SimpleInternationalString;
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

public class DataTestUtil
{
/**
 * FeatureType named "Test" with a {@link Geometry} GEOM field
 * @return
 */
	static public SimpleFeatureType createTest2FeatureType()
	{
		SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
	
		//set the name
		b.setName( "Test" );
	
		//add some properties
		b.add( "name", String.class );
		b.add( "classification", Integer.class );
		b.add( "height", Double.class );
	
		//add a geometry property
		b.setCRS( DefaultGeographicCRS.WGS84 ); // set crs first
		b.add( "GEOM", Geometry.class ); // then add geometry
	
		//build the type
		return b.buildFeatureType();
	}

	static public SimpleFeatureCollection createTest2Collection()
	{
		SimpleFeatureType type= createTest2FeatureType();
		SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
		GeometryFactory geomFactory = JTSFactoryFinder.getGeometryFactory();
		//add the values
		builder.add( "Canada" );
		builder.add( 1 );
		builder.add( 20.5 );
		builder.add(geomFactory.createPoint(new Coordinate(40,0)) );
	
		//build the feature with provided ID
		SimpleFeature feature = builder.buildFeature( "fid.1" );
		DefaultFeatureCollection featureCollection=new DefaultFeatureCollection("TestCollection",type);
		featureCollection.add(feature);
		
		builder.add( "MyHome" );
		builder.add( 12 );
		builder.add( 40.5 );
		builder.add(geomFactory.createLineString(new Coordinate[]{new Coordinate(40,0), new Coordinate(42,0)}) );
	
		//build the feature with provided ID
		SimpleFeature feature2 = builder.buildFeature( "fid.2" );
		featureCollection.add(feature2);
		return featureCollection;
	}

	/**
	 * Feature collection with FeatureType named "Simple"
	 */
	static public SimpleFeatureCollection createTestFeatureCollection()
	{
		FeatureId fid=new FeatureIdImpl("TestFID");
		SimpleFeatureType featureType=createTestFeatureType();
		
		List<Object> atrs=new ArrayList<Object>();
		atrs.add("string value 1");
		atrs.add("string value 2");
		atrs.add("string value 3");
		//GeometryBuilder factory=new GeometryBuilder(DefaultGeographicCRS.WGS84);	
		GeometryFactory geomFact=new GeometryFactory();
		CoordinateSequence coords=new CoordinateArraySequence(new Coordinate[]{new Coordinate(40,-10)});
		Point pointJTS=new Point(coords,geomFact);
		atrs.add(pointJTS);// Geometry
		
		SimpleFeature feat1=new SimpleFeatureImpl(atrs, featureType, fid);
		DefaultFeatureCollection featureCollection=new DefaultFeatureCollection("TestCollection",featureType);
		featureCollection.add(feat1);
		
		return featureCollection;
	}

	/**
	 * @return
	 */
	public static SimpleFeatureType createTestFeatureType()
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
