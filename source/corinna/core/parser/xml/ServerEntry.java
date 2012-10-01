package corinna.core.parser.xml;


import java.util.LinkedList;
import java.util.List;

import corinna.core.IBasicConfig;


public class ServerEntry extends BasicEntry
{

	private List<String> services;
	
	private List<String> connectors;

	public ServerEntry( String className, IBasicConfig config )
	{
		super(className, config);
		services = new LinkedList<String>();
		connectors = new LinkedList<String>();
	}

	public void addService( String name )
	{
		services.add(name);
	}

	public void addConnector( String name )
	{
		connectors.add(name);
	}
	
	public List<String> getServices()
	{
		return services;
	}

	public List<String> getConnectors()
	{
		return connectors;
	}
	
}
