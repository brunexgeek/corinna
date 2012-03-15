package corinna.util;

import javax.bindlet.IComponentInformation;


public class ComponentInformation implements IComponentInformation
{

	private String name;
	
	private String version;
	
	private String implementor;
	
	public ComponentInformation( String name, String version, String implementor )
	{
		this.name = name;
		this.version = version;
		this.implementor = implementor;
	}
	
	@Override
	public String getComponentName()
	{
		return name;
	}

	@Override
	public String getComponentVersion()
	{
		return version;
	}

	@Override
	public String getComponentImplementor()
	{
		return implementor;
	}

}
