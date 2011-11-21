package es.uva.idelab.featurepub.process.data;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class DataUtilities {

	public SimpleFeature addAttribute(SimpleFeature feature, String attributeName, Object attributeValue){
		
		SimpleFeatureType featureType = feature.getFeatureType();
		SimpleFeatureTypeBuilder featureTypeBuilder = new SimpleFeatureTypeBuilder();
		
		featureTypeBuilder.init(featureType);
		featureTypeBuilder.add(attributeName, attributeValue.getClass());


		SimpleFeatureType featureWithAttributteType = featureTypeBuilder.buildFeatureType();
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureWithAttributteType);
		
		featureBuilder.init(feature);
		featureBuilder.set(attributeName, attributeValue);
		
		SimpleFeature featureWithAttributte = featureBuilder.buildFeature(feature.getID());
		return featureWithAttributte;
	}

}
