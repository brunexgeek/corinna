package corinna.core.parser.xml;


import java.util.LinkedList;
import java.util.List;


public class ServerEntry extends BasicEntry
{

	private List<String> services;

	public ServerEntry( String name, String className )
	{
		super(name, className);
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
