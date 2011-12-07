package es.uva.idelab.featurepub.publisher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;

import es.uva.idelab.featurepub.encoder.Encoder;
import es.uva.idelab.featurepub.process.Process;
import es.uva.idelab.featurepub.producer.Producer;

public class BasicPublisher implements  Publisher
{

	private static final Log						logger					=LogFactory.getLog(BasicPublisher.class);

	Query											query;
	DataStore										dataStore;
	/**
	 * Ordered list of processes that filters the features sequentially.
	 */
	List<Process>									processes				=new ArrayList<Process>();
	Map<String, Encoder>							encoderMap				=new HashMap<String, Encoder>();

	/**
	 * Algorithm for generating documents out of the queries
	 */
	Producer										producer;
	/**
	 * Cached instance of the FeatureSource this Publisher points to
	 */
	private SimpleFeatureSource	featureSource;
	
	/*
	 * Returns a cached FeatureSource representing a Typename in a Datastore
	 * 
	 * @see es.uva.idelab.featurepub.publisher.Publisher#getFeatureSource()
	 */
	@Override
	public SimpleFeatureSource getFeatureSource() throws IOException
	{
		if (featureSource==null)
		{
			Query query=this.getQuery();
			String typeName=query.getTypeName();
			DataStore dataStore=this.getDataStore();
			this.featureSource=dataStore.getFeatureSource(typeName);
		}
		return this.featureSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.uva.idelab.featurepub.publisher.Publisher#setQuery(org.geotools.data
	 * .Query)
	 */
	@Override
	public void setQuery(Query query)
	{
		this.query=query;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.uva.idelab.featurepub.publisher.Publisher#setProcesses(java.util.List)
	 */
	@Override
	public void setProcesses(List<Process> processes)
	{
		this.processes=processes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.uva.idelab.featurepub.publisher.Publisher#putEncoder(java.lang.String,
	 * es.uva.idelab.featurepub.encoder.Encoder)
	 */
	@Override
	public void putEncoder(String name, Encoder encoder)
	{
		this.encoderMap.put(name, encoder);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.uva.idelab.featurepub.publisher.Publisher#getEncoder(java.lang.String)
	 */
	@Override
	public Encoder getEncoder(String name)
	{
		return this.encoderMap.get(name);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uva.idelab.featurepub.publisher.Publisher#getDataStore()
	 */
	@Override
	public DataStore getDataStore()
	{
		return dataStore;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uva.idelab.featurepub.publisher.Publisher#getQuery()
	 */
	@Override
	public Query getQuery()
	{
		return query;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uva.idelab.featurepub.publisher.Publisher#getProcesses()
	 */
	@Override
	public List<Process> getProcesses()
	{
		return processes;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uva.idelab.featurepub.publisher.Publisher#getProducer()
	 */
	@Override
	public Producer getProducer()
	{
		return producer;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.uva.idelab.featurepub.publisher.Publisher#setProducer(es.uva.idelab
	 * .featurepub.producer.Producer)
	 */
	@Override
	public void setProducer(Producer producer)
	{
		this.producer=producer;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.uva.idelab.featurepub.publisher.Publisher#setDataStore(org.geotools
	 * .data.DataStore)
	 */
	@Override
	public void setDataStore(DataStore store)
	{
		this.dataStore=store;
	}

}
