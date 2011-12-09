package es.uva.idelab.featurepub;

import java.util.Map;

import es.uva.idelab.featurepub.encoder.Encoder;
import es.uva.idelab.featurepub.publisher.Publisher;

public interface FeaturePublisherConfiguration
{

	public abstract void setCreditsText(String creditsText);

	public abstract String getCreditsText();

	public abstract void setAdminEmail(String adminEmail);

	public abstract String getAdminEmail();

	public abstract void setDescription(String description);

	public abstract String getDescription();

	public abstract void setServerName(String serverName);

	public abstract String getServerName();

	public abstract void setAvailableEncoders(Map<String, Encoder> availableEncoders);

	public abstract Map<String, Encoder> getAvailableEncoders();

	public abstract void setAvailablePublishers(Map<String, Publisher> availablePublishers);

	public abstract Map<String, Publisher> getAvailablePublishers();

}
