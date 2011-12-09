package es.uva.idelab.featurepub;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import es.uva.idelab.featurepub.encoder.Encoder;
import es.uva.idelab.featurepub.publisher.Publisher;

public class SimpleFeaturePubConfiguration implements FeaturePublisherConfiguration
{
	@Autowired
	Map<String,Publisher> availablePublishers;
	@Autowired
	Map<String, Encoder> availableEncoders;
	
	/**
	 * Service Configuration
	 */
	String serverName;
	String description;
	String adminEmail;
	String creditsText;
	/**
	 * @return the availablePublishers
	 */
	@Override
	public Map<String, Publisher> getAvailablePublishers()
	{
		return availablePublishers;
	}
	/**
	 * @param availablePublishers the availablePublishers to set
	 */
	@Override
	public void setAvailablePublishers(Map<String, Publisher> availablePublishers)
	{
		this.availablePublishers=availablePublishers;
	}
	/**
	 * @return the availableEncoders
	 */
	@Override
	public Map<String, Encoder> getAvailableEncoders()
	{
		return availableEncoders;
	}
	/**
	 * @param availableEncoders the availableEncoders to set
	 */
	@Override
	public void setAvailableEncoders(Map<String, Encoder> availableEncoders)
	{
		this.availableEncoders=availableEncoders;
	}
	/**
	 * @return the serverName
	 */
	@Override
	public String getServerName()
	{
		return serverName;
	}
	/**
	 * @param serverName the serverName to set
	 */
	@Override
	public void setServerName(String serverName)
	{
		this.serverName=serverName;
	}
	/**
	 * @return the description
	 */
	@Override
	public String getDescription()
	{
		return description;
	}
	/**
	 * @param description the description to set
	 */
	@Override
	public void setDescription(String description)
	{
		this.description=description;
	}
	/**
	 * @return the adminEmail
	 */
	@Override
	public String getAdminEmail()
	{
		return adminEmail;
	}
	/**
	 * @param adminEmail the adminEmail to set
	 */
	@Override
	public void setAdminEmail(String adminEmail)
	{
		this.adminEmail=adminEmail;
	}
	/**
	 * @return the creditsText
	 */
	@Override
	public String getCreditsText()
	{
		return creditsText;
	}
	/**
	 * @param creditsText the creditsText to set
	 */
	@Override
	public void setCreditsText(String creditsText)
	{
		this.creditsText=creditsText;
	}
	
}
