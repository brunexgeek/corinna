package corinna.core.parser.xml;

import java.util.LinkedList;
import java.util.List;

import corinna.core.INetworkConnectorConfig;


public class ConnectorEntry extends BasicEntry
{
	
	private List<String> adapters;

	public ConnectorEntry( String className, INetworkConnectorConfig config )
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
