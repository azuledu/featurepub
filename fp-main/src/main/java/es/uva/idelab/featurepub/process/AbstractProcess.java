package es.uva.idelab.featurepub.process;

import org.opengis.feature.simple.SimpleFeature;

public abstract class AbstractProcess implements Process
{

	public AbstractProcess()
	{
		super();
	}

	public abstract SimpleFeature processFeature(SimpleFeature feature);

	@Override
	public String getKeyString()
	{
		return this.getClass().getName();
	}

}