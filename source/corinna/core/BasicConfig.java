package corinna.core;

import corinna.exception.ConfigurationNotFoundException;
import corinna.util.conf.ISection;


public abstract class BasicConfig implements IBasicConfig
{

	protected ISection section;
	
	protected String name;

	public BasicConfig( String name )
	{
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("The name can not be null or empty");
		
		this.name = name;
	}
	
	@Override
	public String getParameter( String name ) throws ConfigurationNotFoundException
	{
		return section.getValue(name);
	}

	@Override
	public String[] getParameterNames()
	{
		return section.getKeys();
	}

	@Override
	public ISection getSection()
	{
		return section;
	}

	@Override
	public String getParameter( String name, String defaultValue )
	{
		return section.getValue(name, defaultValue);
	}

	@Override
	public void setParameter( String name, String value )
	{
		section.setValue(name, value);
	}
	
	@Override
	public boolean containsParameter( String name )
	{
		return section.containsKey(name);
	}
	
}
