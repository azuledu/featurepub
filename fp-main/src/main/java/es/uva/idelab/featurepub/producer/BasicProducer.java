package es.uva.idelab.featurepub.producer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import es.uva.idelab.featurepub.encoder.Encoder;
import es.uva.idelab.featurepub.process.Process;
import es.uva.idelab.featurepub.publisher.Publisher;



public class BasicProducer implements Producer {
	
	Cache cache;
	/**
	 * Apply BBOX filter in the feature-processing loop. This is needed if Datastore does not honor Filter section i.e. Oracle9i in Geotools
	 */
	private boolean	filterInLoop=false;

	public BasicProducer(CacheManager cacheManager) {
		this.cache = cacheManager.getCache("myCache"); // TODO configure cache outside
		if (this.cache==null)
			{
			cacheManager.addCache("myCache");
			this.cache=cacheManager.getCache("myCache");
			}
	}
	/**^
	 * Fills the {@link Map} parameters with the entries to represent a BBOX
	 * @param bboxParam
	 * @param parameters
	 */
	public static void setBboxParams(String bboxParam, Map<String,Object> parameters )
	{
		if (bboxParam != null) {
			// BBOX=[longitude_west, latitude_south, longitude_east, latitude_north]
			String[] bboxParams = bboxParam.split(",");
			double xMin = Double.valueOf(bboxParams[0]).doubleValue();
			double yMin = Double.valueOf(bboxParams[1]).doubleValue();
			double xMax = Double.valueOf(bboxParams[2]).doubleValue();
			double yMax = Double.valueOf(bboxParams[3]).doubleValue();
			parameters.put(PARAM_XMAX, xMax);
			parameters.put(PARAM_XMIN, xMin);
			parameters.put(PARAM_YMAX,yMax);
			parameters.put(PARAM_YMIN,yMin);
		}
	}
	
	/* (non-Javadoc)
	 * @see es.uva.idelab.featurepub.producer.producer#produceDocument(es.uva.idelab.featurepub.Publisher)
	 */
	@Override
	public void produceDocument(OutputStream output, Publisher publisher, Map<String, Object> parameters, Encoder encoderBase) throws IOException
	{
		// Gets a new instance to process this thread
		Encoder encoder=createEncoderInstance(encoderBase);	
		encoder.setOutputStream(output);
		ProducerContext producerContext=new SimpleProducerContext(output, publisher,parameters,encoder);
		encoder.startDocument("Feature Publish");

		// TODO intentar meterlo en el bean. No sirve con añadir
		// <property name="coordinateSystemReproject" value="4326" />
		// porque no Spring no sabe convertir de String a
		// CoordinateReferenceSystem
		// query.setCoordinateSystemReproject(WGS84); //No recupera features..
		// query.setCoordinateSystem(WGS84); //No recupera features..
		
		List<Process> processes=publisher.getProcesses();
		
		SimpleFeatureCollection features=getFeatureCollection(producerContext);
		/**
		 * Filter to apply locally @see filterInLoop
		 **/
		Filter bboxFilter=createBBoxFilter(producerContext);

		SimpleFeatureIterator featuresIterator = features.features();
		try {
			while (featuresIterator.hasNext())
			{
				SimpleFeature feature = featuresIterator.next();

				String key = feature.getID() + processes.toString();

				if (cache.get(key) == null) {
					/**
					 * BBOX filtering in Query when functional. i.e. With Oracle9i geotools BBOX implementation does not work
					 * @see this.filterInLoop
					 */
					if (this.filterInLoop)
					{
					if (!bboxFilter.evaluate(feature)) 
						{
						System.out.println("Feature outside BBOX (not printed): " + feature.getID());
						continue;
						}
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

	/**
	 * @param encoderBase
	 * @param encoder
	 * @return
	 */
	private Encoder createEncoderInstance(Encoder encoderBase)
	{
		try
		{
			return encoderBase.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return the cache
	 */
	public Cache getCache()
	{
		return cache;
	}

	/**
	 * Perform a query against the {@link FeatureSource}
	 * Include a set of filters in the Query.
	 * @param publisher
	 * @param parameters
	 * @param featureSource
	 * @return
	 * @throws IOException
	 */
	protected SimpleFeatureCollection getFeatureCollection(ProducerContext context)
		throws IOException
	{
		Query query = configureQuery(context);
		SimpleFeatureSource featureSource=context.getPublisher().getFeatureSource();
		// TODO check this utility class
		// http://docs.geotools.org/latest/javadocs/org/geotools/data/collection/SpatialIndexFeatureSource.html
		SimpleFeatureCollection features = featureSource.getFeatures(query);
		return features;
	}

	/**
	 * Constructs a spatial filter using parameters from the map {@link ProducerContext#getParameters()}
	 *
	 *		{@link Producer#PARAM_XMIN}
	 *		{@link Producer#PARAM_XMAX}
	 *		{@link Producer#PARAM_YMIN}
	 *		{@link Producer#PARAM_YMAX}
	 * @param context holder for current request configurations.
	 * @return
	 */
	private Filter createBBoxFilter(ProducerContext context)
	{
		try
		{
			Map<String, Object> parameters=context.getParameters();
			
			if (!parameters.containsKey(PARAM_XMAX) ||
				!parameters.containsKey(PARAM_XMIN) ||
				!parameters.containsKey(PARAM_YMAX) ||
				!parameters.containsKey(PARAM_YMIN) )
				{
				// There are no BBOX parameters
					return null;
				}
				
			double xMin=(Double) parameters.get(Producer.PARAM_XMIN);
			double xMax=(Double) parameters.get(Producer.PARAM_XMAX);
			double yMin=(Double) parameters.get(Producer.PARAM_YMIN);
			double yMax=(Double) parameters.get(Producer.PARAM_YMAX);
			
			FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(null);
			SimpleFeatureType schema = context.getPublisher().getFeatureSource().getSchema();
			String geometryPropertyName = schema.getGeometryDescriptor().getLocalName();
			CoordinateReferenceSystem targetCRS = schema.getGeometryDescriptor().getCoordinateReferenceSystem();
			ReferencedEnvelope bbox=new ReferencedEnvelope(xMin, xMax, yMin, yMax, targetCRS);
			Filter bboxFilter = filterFactory.bbox(filterFactory.property(geometryPropertyName), bbox);
			return bboxFilter;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}


	/**
	 * Create a copy of the {@link Publisher#getQuery()}
	 * Setup additional filters to the query.
	 * Include mandatory properties of the feature.
	 * 
	 * @param publisher
	 * @param parameters 
	 * @return {@link Query} with extra filtering.
	 * @throws IOException 
	 */
	private Query configureQuery(ProducerContext context) throws IOException
	{
		Publisher publisher=context.getPublisher();
		
		Query templateQuery=publisher.getQuery();
		Query query= new Query(templateQuery);
		
		addQueryFilters(query,context);
		Object includeProps = query.getHints().get(Query.INCLUDE_MANDATORY_PROPS);
		if (includeProps instanceof Boolean && ((Boolean) includeProps).booleanValue())
		{
			SimpleFeatureType featureType = publisher.getFeatureSource().getSchema();
			query.setProperties(DataUtilities.addMandatoryProperties(featureType, query.getProperties()));
		}
		return query;
	}
	/**
	 * Add a set of filters to the query.
	 * Filters can be constructed from parameters. i.e. BBOX filters
	 *  Currently this adds to the filter list:
	 *  	- BBOX filter according to parameters {@link Producer#PARAM_XMAX},{@link Producer#PARAM_XMIN},etc
	 *  	- merge a filter list stored in parameters with key {@link Producer#PARAM_FILTERLIST} (Probably set by any request dispatcher.)
	 * @param query
	 * @param parameters
	 * @throws IOException
	 */
	protected void addQueryFilters(Query query, ProducerContext context) throws IOException
	{		
		List<Filter> filters = new ArrayList<Filter>();

		FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(null);

		// Query Filter
		Filter queryFilter = query.getFilter();
		if (queryFilter != null)
			filters.add(queryFilter);

		// No funciona con Oracle 9i (precisamente)
/*		// BBox Filter 
		SimpleFeatureType schema = featureSource.getSchema();
		String geometryPropertyName = schema.getGeometryDescriptor().getLocalName();
		CoordinateReferenceSystem targetCRS = schema.getGeometryDescriptor().getCoordinateReferenceSystem();
		this.bbox = new ReferencedEnvelope(-80, 80, -40, 40, targetCRS);
		Filter bboxFilter = filterFactory.bbox(filterFactory.property(geometryPropertyName), bbox);
		if (bboxFilter != null)
			filters.add(bboxFilter);
*/
		// ECQL Filters
		// ..
		// if (ecqlFilter != null) filters.add(ecqlFilter);
		
		/**
		 * Spatial filter
		 */
		Filter bboxFilter=createBBoxFilter(context);
		if (bboxFilter!=null)
			filters.add(bboxFilter);
		
		if (filters.size() == 0)
			query.setFilter(Filter.INCLUDE);
		else if (filters.size() == 1)
			query.setFilter((Filter) filters.get(0));
		else
			query.setFilter(filterFactory.and(filters));
	}

}
