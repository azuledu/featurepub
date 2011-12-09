package es.uva.idelab.geoserver.datasource;

import java.io.IOException;
import java.util.List;

import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.config.GeoServer;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureWriter;
import org.geotools.data.LockingManager;
import org.geotools.data.Query;
import org.geotools.data.ServiceInfo;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;



public class GeoserverDataSource implements DataStore {
	protected DataStore datastore=null;
	protected DataStoreInfo datastoreInfo;
	void geoserverDataSource(GeoServer gconfig, String workspace,String datastoreName) throws IOException
	{
		this.datastoreInfo= gconfig.getCatalog().getDataStoreByName(datastoreName);
		this.datastore = datastoreInfo.getDataStore(null);
	}
	/**
	 * @param typeName
	 * @param featureType
	 * @throws IOException
	 * @see org.geotools.data.DataStore#updateSchema(java.lang.String, org.opengis.feature.simple.SimpleFeatureType)
	 */
	public void updateSchema(String typeName, SimpleFeatureType featureType) throws IOException
	{
		datastore.updateSchema(typeName, featureType);
	}
	/**
	 * @return
	 * @throws IOException
	 * @see org.geotools.data.DataStore#getTypeNames()
	 */
	public String[] getTypeNames() throws IOException
	{
		return datastore.getTypeNames();
	}
	/**
	 * @param typeName
	 * @return
	 * @throws IOException
	 * @see org.geotools.data.DataStore#getSchema(java.lang.String)
	 */
	public SimpleFeatureType getSchema(String typeName) throws IOException
	{
		return datastore.getSchema(typeName);
	}
	/**
	 * @param typeName
	 * @return
	 * @throws IOException
	 * @see org.geotools.data.DataStore#getFeatureSource(java.lang.String)
	 */
	public SimpleFeatureSource getFeatureSource(String typeName) throws IOException
	{
		return datastore.getFeatureSource(typeName);
	}
	/**
	 * @param typeName
	 * @return
	 * @throws IOException
	 * @see org.geotools.data.DataStore#getFeatureSource(org.opengis.feature.type.Name)
	 */
	public SimpleFeatureSource getFeatureSource(Name typeName) throws IOException
	{
		return datastore.getFeatureSource(typeName);
	}
	/**
	 * @return
	 * @see org.geotools.data.DataAccess#getInfo()
	 */
	public ServiceInfo getInfo()
	{
		return datastore.getInfo();
	}
	/**
	 * @param query
	 * @param transaction
	 * @return
	 * @throws IOException
	 * @see org.geotools.data.DataStore#getFeatureReader(org.geotools.data.Query, org.geotools.data.Transaction)
	 */
	public FeatureReader<SimpleFeatureType, SimpleFeature> getFeatureReader(Query query, Transaction transaction) throws IOException
	{
		return datastore.getFeatureReader(query, transaction);
	}
	/**
	 * @param featureType
	 * @throws IOException
	 * @see org.geotools.data.DataAccess#createSchema(org.opengis.feature.type.FeatureType)
	 */
	public void createSchema(SimpleFeatureType featureType) throws IOException
	{
		datastore.createSchema(featureType);
	}
	/**
	 * @param typeName
	 * @param featureType
	 * @throws IOException
	 * @see org.geotools.data.DataAccess#updateSchema(org.opengis.feature.type.Name, org.opengis.feature.type.FeatureType)
	 */
	public void updateSchema(Name typeName, SimpleFeatureType featureType) throws IOException
	{
		datastore.updateSchema(typeName, featureType);
	}
	/**
	 * @return
	 * @throws IOException
	 * @see org.geotools.data.DataAccess#getNames()
	 */
	public List<Name> getNames() throws IOException
	{
		return datastore.getNames();
	}
	/**
	 * @param typeName
	 * @param filter
	 * @param transaction
	 * @return
	 * @throws IOException
	 * @see org.geotools.data.DataStore#getFeatureWriter(java.lang.String, org.opengis.filter.Filter, org.geotools.data.Transaction)
	 */
	public FeatureWriter<SimpleFeatureType, SimpleFeature> getFeatureWriter(String typeName, Filter filter, Transaction transaction)
		throws IOException
	{
		return datastore.getFeatureWriter(typeName, filter, transaction);
	}
	/**
	 * @param name
	 * @return
	 * @throws IOException
	 * @see org.geotools.data.DataAccess#getSchema(org.opengis.feature.type.Name)
	 */
	public SimpleFeatureType getSchema(Name name) throws IOException
	{
		return datastore.getSchema(name);
	}
	/**
	 * @param typeName
	 * @param transaction
	 * @return
	 * @throws IOException
	 * @see org.geotools.data.DataStore#getFeatureWriter(java.lang.String, org.geotools.data.Transaction)
	 */
	public FeatureWriter<SimpleFeatureType, SimpleFeature> getFeatureWriter(String typeName, Transaction transaction) throws IOException
	{
		return datastore.getFeatureWriter(typeName, transaction);
	}
	/**
	 * 
	 * @see org.geotools.data.DataAccess#dispose()
	 */
	public void dispose()
	{
		datastore.dispose();
	}
	/**
	 * @param typeName
	 * @param transaction
	 * @return
	 * @throws IOException
	 * @see org.geotools.data.DataStore#getFeatureWriterAppend(java.lang.String, org.geotools.data.Transaction)
	 */
	public FeatureWriter<SimpleFeatureType, SimpleFeature> getFeatureWriterAppend(String typeName, Transaction transaction) throws IOException
	{
		return datastore.getFeatureWriterAppend(typeName, transaction);
	}
	/**
	 * @return
	 * @see org.geotools.data.DataStore#getLockingManager()
	 */
	public LockingManager getLockingManager()
	{
		return datastore.getLockingManager();
	}
	
	
}
