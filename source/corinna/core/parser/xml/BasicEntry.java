package corinna.core.parser.xml;

import corinna.core.IBasicConfig;


public class BasicEntry
{

	private IBasicConfig config;

	private String className;

	public BasicEntry( String className, IBasicConfig config )
	{
		if (config == null)
			throw new IllegalArgumentException("The configuration object can not be null");
		if (className == null || className.isEmpty())
			throw new IllegalArgumentException("The class name can not be null or empty");
		
		this.config = config;
		this.className = className;
	}

	public IBasicConfig getConfig()
	{
		return config;
	}

	public String getClassName()
	{
		return className;
	}
	
}
