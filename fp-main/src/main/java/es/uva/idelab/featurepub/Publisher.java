package es.uva.idelab.featurepub;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.sld.v1_1.SLDConfiguration;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.xml.Configuration;
import org.geotools.xml.Parser;

import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.springframework.beans.factory.InitializingBean;

import es.uva.idelab.featurepub.encoder.Encoder;
import es.uva.idelab.featurepub.encoder.kml.KmlEncoder;
import es.uva.idelab.featurepub.process.Process;

public class Publisher implements InitializingBean {

	private static final Log logger = LogFactory.getLog(Publisher.class);

	private static final CoordinateReferenceSystem WGS84;

	static {
		try {
			WGS84 = CRS.decode("EPSG:4326");
		} catch (Exception e) {
			throw new RuntimeException("Cannot decode EPSG:4326, the CRS subsystem must be badly broken...");
		}
	}

	private String outFile;

	Query query;
	List<Process> processes;
	Map<String, Object> connectionParameters = new HashMap<String,Object>();
	DataStore dataStore;
	SimpleFeatureSource featureSource;
	Map<String,Encoder> encoderMap = new HashMap<String,Encoder>();
	FeatureTypeStyle featureTypeStyle;


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

	public void setQuery(Query query) {
		this.query = query;
	}

	public void setProcesses(List<Process> processes) {
		this.processes = processes;
	}

	public void setConnectionParameters(Map<String, Object> connectionParameters) {
		this.connectionParameters = connectionParameters;
	}

	public void putEncoder(String name, Encoder encoder) {
		this.encoderMap.put (name,encoder);
		
	}

	public Encoder getEncoder(String name) {
		return this.encoderMap.get(name);
	}

	public Encoder getNewEncoderInstance(String name) {
		return (Encoder) ((Object) getEncoder(name)).clone();
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
				logger.debug("Parse failed");
			}
		}
	}

	public void produceDocument(String requestString) {
		KmlEncoder kmlEncoder = (KmlEncoder) encoder;

		encoder.startDocument("Feature Publish");
		kmlEncoder.putNetworklink(requestString);
		encoder.endDocument();
	}

}
