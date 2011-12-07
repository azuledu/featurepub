package es.uva.idelab.featurepub.publisher;

import java.io.IOException;
import java.util.List;

import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;

import es.uva.idelab.featurepub.encoder.Encoder;
import es.uva.idelab.featurepub.process.Process;
import es.uva.idelab.featurepub.producer.Producer;

public interface Publisher
{

	/**
	 * Create a new FeatureSource representing a TypeName stored in the datastore.
	 * @param query filters for selecting features
	 */
	public abstract SimpleFeatureSource getFeatureSource() throws IOException;

	/* (non-Javadoc)
	 * @see es.uva.idelab.featurepub.Producer#setQuery(org.geotools.data.Query)
	 */
	public abstract void setQuery(Query query);

	/* (non-Javadoc)
	 * @see es.uva.idelab.featurepub.Producer#setProcesses(java.util.List)
	 */
	public abstract void setProcesses(List<Process> processes);

	/* (non-Javadoc)
	 * @see es.uva.idelab.featurepub.Producer#putEncoder(java.lang.String, es.uva.idelab.featurepub.encoder.Encoder)
	 */
	public abstract void putEncoder(String name, Encoder encoder);

	/* (non-Javadoc)
	 * @see es.uva.idelab.featurepub.Producer#getEncoder(java.lang.String)
	 */
	public abstract Encoder getEncoder(String name);

	/* (non-Javadoc)
	 * @see es.uva.idelab.featurepub.Producer#getDataStore()
	 */
	public abstract DataStore getDataStore();

	/* (non-Javadoc)
	 * @see es.uva.idelab.featurepub.Producer#getQuery()
	 */
	public abstract Query getQuery();

	/* (non-Javadoc)
	 * @see es.uva.idelab.featurepub.Producer#getProcesses()
	 */
	public abstract List<Process> getProcesses();

	/* (non-Javadoc)
	 * @see es.uva.idelab.featurepub.Producer#getProducer()
	 */
	public abstract Producer getProducer();

	/* (non-Javadoc)
	 * @see es.uva.idelab.featurepub.Producer#setProducer(es.uva.idelab.featurepub.producer.Producer)
	 */
	public abstract void setProducer(Producer producer);

	/* (non-Javadoc)
	 * @see es.uva.idelab.featurepub.Producer#setDataStore(org.geotools.data.DataStore)
	 */
	public abstract void setDataStore(DataStore store);

}