package corinna.core.parser.xml;


import java.util.LinkedList;
import java.util.List;

import corinna.util.conf.ISection;


public class ServerEntry extends BindletEntry
{

	private List<String> services;

	public ServerEntry( String name, String className, ISection config )
	{
		super(name, className, config);
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
