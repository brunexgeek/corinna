package corinna.core.parser.xml;


import java.util.LinkedList;
import java.util.List;

import corinna.core.IBasicConfig;


public class ServerEntry extends BasicEntry
{

	private List<String> services;

	public ServerEntry( String className, IBasicConfig config )
	{
		super(className, config);
		services = new LinkedList<String>();
	}

	public void addService( String name )
	{
		services.add(name);
	}

	public List<String> getServices()
	{
		return services;
	}

}
