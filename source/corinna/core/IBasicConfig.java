package corinna.core;

import corinna.exception.ConfigurationNotFoundException;
import corinna.util.conf.ISection;


public interface IBasicConfig
{

	public String getParameter( String name ) throws ConfigurationNotFoundException;
	
	public String getParameter( String name, String defaultValue );

	public String[] getParameterNames();
	
	public boolean containsParameter( String name );
	
	public ISection getSection();

	void setParameter( String name, String value );
	
}
