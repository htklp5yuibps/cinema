package no.comp.cinema.configuration;

import java.util.ResourceBundle;

public class PropertiesManager {
	private static final PropertiesManager instance
			= new PropertiesManager();
	
	private static final String CONFIGURATION_FILE_NAME = "configuration";
	
	public static PropertiesManager getInstance() {
		return instance;
	}
	
	private ResourceBundle props;
	
	private PropertiesManager() {
		this.props = ResourceBundle.getBundle(CONFIGURATION_FILE_NAME);
	}
	
	public String getPropertyValue(String propertyKey) {
		return props.getString(propertyKey);
	}
	
}
