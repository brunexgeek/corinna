package corinna.core.parser.xml;

import corinna.util.conf.ISection;


public class BindletEntry
{
	private ISection config;

	private String className;
	
	private String name;

	public BindletEntry( String name, String className, ISection config )
	{
		if (config == null)
			throw new IllegalArgumentException("The configuration object can not be null");
		if (className == null || className.isEmpty())
			throw new IllegalArgumentException("The class name can not be null or empty");
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("The bindlet name can not be null or empty");
		
		this.config = config;
		this.className = className;
		this.name = name;
	}

	public ISection getConfig()
	{
		return config;
	}

	public String getClassName()
	{
		return className;
	}

	public String getName()
	{
		return name;
	}
}
