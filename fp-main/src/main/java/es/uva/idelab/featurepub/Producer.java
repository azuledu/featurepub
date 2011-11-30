package es.uva.idelab.featurepub;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.sld.v1_1.SLDConfiguration;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.xml.Configuration;
import org.geotools.xml.Parser;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.springframework.beans.factory.InitializingBean;

import es.uva.idelab.featurepub.encoder.Encoder;
import es.uva.idelab.featurepub.encoder.kml.KmlEncoder;
import es.uva.idelab.featurepub.process.Process;

public class Producer implements InitializingBean {

	private static final Log logger = LogFactory.getLog(Producer.class);

	private static final CoordinateReferenceSystem WGS84;

	static {
		try {
			WGS84 = CRS.decode("EPSG:4326");
		} catch (Exception e) {
			throw new RuntimeException("Cannot decode EPSG:4326, the CRS subsystem must be badly broken...");
		}
	}

	private String outFile;
	ReferencedEnvelope bbox;
	private double xMin = -170;
	private double xMax = 170;
	private double yMin = -80;
	private double yMax = 80;
	Query query;
	List<Process> processes;
	Map<String, Object> connectionParameters;
	DataStore dataStore;
	SimpleFeatureSource featureSource;
	Encoder encoder;
	FeatureTypeStyle featureTypeStyle;

	Cache cache;

	public Producer(CacheManager cacheManager) {
		this.cache = cacheManager.getCache("myCache");
	}

	public void afterPropertiesSet() throws IOException {
		dataStore = DataStoreFinder.getDataStore(connectionParameters);
		if (dataStore == null) {
			if (logger.isDebugEnabled())
				logger.debug("Could not connect - check parameters");
		}
	}

	public void setOutFile(String outFile) {
		this.outFile = outFile;
	}

	public String getOutFile() {
		return outFile;
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

	public void setQuery(Query query) {
		this.query = query;
	}

	public void setProcesses(List<Process> processes) {
		this.processes = processes;
	}

	public void setConnectionParameters(Map<String, Object> connectionParameters) {
		this.connectionParameters = connectionParameters;
	}

	public void setEncoder(Encoder encoder) {
		this.encoder = encoder;
	}

	public Encoder getEncoder() {
		return encoder;
	}

	public void setQueryFilters() throws IOException {
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

		if (filters.size() == 0)
			query.setFilter(Filter.INCLUDE);
		else if (filters.size() == 1)
			query.setFilter((Filter) filters.get(0));
		else
			query.setFilter(filterFactory.and(filters));
	}

	public void setFeatureTypeStyleFromSLD(String sld) {
		try {
			FileReader fileReader = new FileReader(sld);
			Configuration config = new SLDConfiguration();
			Parser parser = new Parser(config);
			FeatureTypeStyle featureTypeStyle = (FeatureTypeStyle) parser.parse(fileReader);
			this.featureTypeStyle = featureTypeStyle;

		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Parseo fallido");
			}
		}
	}

	public void produceDocument(String requestString) {
		KmlEncoder kmlEncoder = (KmlEncoder) encoder;

		encoder.startDocument("Tematicos");
		kmlEncoder.putNetworklink(requestString);
		encoder.endDocument();
	}

	public void produceDocument() throws Exception {

		encoder.startDocument("Tematicos");
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
