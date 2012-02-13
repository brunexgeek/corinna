package corinna.core;

import corinna.exception.ConfigurationNotFoundException;
import corinna.util.conf.ISection;


public interface IServiceConfig extends IBasicConfig
{

	public String getServiceName();

}
