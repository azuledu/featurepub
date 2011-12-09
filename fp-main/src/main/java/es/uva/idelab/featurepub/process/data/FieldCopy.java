package es.uva.idelab.featurepub.process.data;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import es.uva.idelab.featurepub.process.AbstractProcess;
/**
 * Copy fields in a feature.
 * @author juacas
 *
 */
public class FieldCopy extends AbstractProcess
{
	/**
	 * Source->Target pairs
	 */
	private Map<String, String>	sourceTargetMap;
	/**
	 * Construct a process that copies the values a set of fields into the fields with target names.
	 * @param sourceTargetMap
	 */
	public FieldCopy(Map<String,String> sourceTargetMap)
	{
	this.sourceTargetMap=sourceTargetMap;
	}
	@Override
	public SimpleFeature processFeature(SimpleFeature feature)
	{
		SimpleFeatureType featureType = feature.getFeatureType();
		SimpleFeatureTypeBuilder featureTypeBuilder = new SimpleFeatureTypeBuilder();
		featureTypeBuilder.init(featureType);
		
		Set<Entry<String, String>> entrySet=sourceTargetMap.entrySet();
		
		for (Map.Entry<String, String> entry : entrySet)
		{			
			featureTypeBuilder.add(entry.getValue(), feature.getAttribute(entry.getKey()).getClass());
		}
		SimpleFeatureType featureWithAttributteType = featureTypeBuilder.buildFeatureType();
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureWithAttributteType);
		
		featureBuilder.init(feature);
		for (Map.Entry<String, String> entry : entrySet)
		{
		featureBuilder.set(entry.getValue(), feature.getAttribute(entry.getKey()));
		}
		SimpleFeature featureWithAttributte = featureBuilder.buildFeature(feature.getID());
		return featureWithAttributte;
	}

}
