package corinna.core.parser.xml;

import java.util.LinkedList;
import java.util.List;

import corinna.network.IConnectorConfig;


public class ConnectorEntry extends BasicEntry
{
	
	private List<String> adapters;

	public ConnectorEntry( String className, IConnectorConfig config )
	{
		super(className, config);
		adapters = new LinkedList<String>();
	}

	public void addAdapter( String name )
	{
		adapters.add(name);
	}
	
	public List<String> getAdapters()
	{
		return adapters;
	}
}
