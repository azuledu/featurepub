package es.uva.idelab.featurepub;

import java.io.IOException;
import java.util.List;

import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;

import es.uva.idelab.featurepub.encoder.Encoder;
import es.uva.idelab.featurepub.process.Process;

public interface Producer
{

	public static final String	XMIN	="Xmin";
	public static final String	XMAX	="Xmax";
	public static final String	YMIN	="Ymin";
	public static final String	YMAX	="Ymax";

	/**
	 * Select the typename in the datastore and returns it as a SimpleFeatureSource
	 * @return the featureSource
	 * @throws IOException 
	 */
	public abstract SimpleFeatureSource getFeatureSource() throws IOException;

	public abstract void setQuery(Query query);

	public abstract void setProcesses(List<Process> processes);

	public abstract void putEncoder(String name, Encoder encoder);

	public abstract Encoder getEncoder(String name);

	public abstract DataStore getDataStore();

	public abstract Query getQuery();

	public abstract List<Process> getProcesses();

	/**
	 * @return the producer
	 */
	public abstract Producer getProducer();

	/**
	 * @param producer the producer to set
	 */
	public abstract void setProducer(Producer producer);

	/**
	 * 
	 * @param store
	 */
	public abstract void setDataStore(DataStore store);

}