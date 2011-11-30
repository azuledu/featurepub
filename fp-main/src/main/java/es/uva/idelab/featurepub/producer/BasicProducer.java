package es.uva.idelab.featurepub.producer;

import java.util.ListIterator;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.geotools.data.DataUtilities;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.DataUtilities;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import es.uva.idelab.featurepub.Publisher;
import es.uva.idelab.featurepub.encoder.Encoder;
import es.uva.idelab.featurepub.process.Process;



public class BasicProducer implements producer {

	
	
	ReferencedEnvelope bbox;
	private double xMin = -170;
	private double xMax = 170;
	private double yMin = -80;
	private double yMax = 80;
	
	Cache cache;

	public Publisher(CacheManager cacheManager) {
		this.cache = cacheManager.getCache("myCache");
	}
	
	public void setBbox(String bboxParam) {
		if (bboxParam != null) {
			// BBOX=[longitude_west, latitude_south, longitude_east, latitude_north]
			String[] bboxParams = bboxParam.split(",");
			this.xMin = Double.valueOf(bboxParams[0]).doubleValue();
			this.yMin = Double.valueOf(bboxParams[1]).doubleValue();
			this.xMax = Double.valueOf(bboxParams[2]).doubleValue();
			this.yMax = Double.valueOf(bboxParams[3]).doubleValue();
		}
	}
	
	/* (non-Javadoc)
	 * @see es.uva.idelab.featurepub.producer.producer#produceDocument(es.uva.idelab.featurepub.Publisher)
	 */
	@Override
	public void produceDocument(Publisher publisher, Map<String, Object> parameters) {

		Encoder encoder = publisher.getEncoder();
		
		encoder.startDocument("Feature Publish");
		encoder.putStyles(featureTypeStyle);

		// TODO intentar meterlo en el bean. No sirve con añadir
		// <property name="coordinateSystemReproject" value="4326" />
		// porque no Spring no sabe convertir de String a
		// CoordinateReferenceSystem
		// query.setCoordinateSystemReproject(WGS84); //No recupera features..

		// query.setCoordinateSystem(WGS84); //No recupera features..

		String typeName = query.getTypeName();
		featureSource = dataStore.getFeatureSource(typeName);
		setQueryFilters();

		Object includeProps = query.getHints().get(Query.INCLUDE_MANDATORY_PROPS);
		if (includeProps instanceof Boolean && ((Boolean) includeProps).booleanValue()) {
			SimpleFeatureType featureType = featureSource.getSchema();
			query.setProperties(DataUtilities.addMandatoryProperties(featureType, query.getProperties()));
		}

		// TODO Revisar
		// http://docs.geotools.org/latest/javadocs/org/geotools/data/collection/SpatialIndexFeatureSource.html
		SimpleFeatureCollection features = featureSource.getFeatures(query);

		FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(null);
		SimpleFeatureType schema = featureSource.getSchema();
		String geometryPropertyName = schema.getGeometryDescriptor().getLocalName();
		CoordinateReferenceSystem targetCRS = schema.getGeometryDescriptor().getCoordinateReferenceSystem();
		this.bbox = new ReferencedEnvelope(xMin, xMax, yMin, yMax, targetCRS);
		Filter bboxFilter = filterFactory.bbox(filterFactory.property(geometryPropertyName), bbox);

		SimpleFeatureIterator featuresIterator = features.features();

		try {
			while (featuresIterator.hasNext()) {

				SimpleFeature feature = featuresIterator.next();

				String key = feature.getID() + processes.toString();

				if (cache.get(key) == null) {

					if (!bboxFilter.evaluate(feature)) {
						System.out.println("Feature outside BBOX (not printed): " + feature.getID());
						continue;
					}
					ListIterator<Process> processIterator = processes.listIterator();
					while (processIterator.hasNext()) {
						feature = processIterator.next().processFeature(feature);
					}

					Element geometria_cache = new Element(key, feature);
					cache.put(geometria_cache);
				} else {
					feature = (SimpleFeature) cache.get(key).getObjectValue();
				}

				encoder.encodeFeature(feature);
			}
		} finally {
			featuresIterator.close();
		}
		encoder.endDocument();
	}
	
}
