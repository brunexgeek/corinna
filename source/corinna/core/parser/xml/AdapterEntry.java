package corinna.core.parser.xml;

import java.util.LinkedList;
import java.util.List;

import corinna.network.IAdapterConfig;


public class AdapterEntry extends BasicEntry
{

	private List<String> filters;
	
	public AdapterEntry( String className, IAdapterConfig config )
	{
		super(className, config);
		filters = new LinkedList<String>();
	}

	public List<String> getFilters()
	{
		return filters;
	}

	public void addFilter( String className )
	{
		if (className == null || className.isEmpty()) return;
		filters.add(className);
	}
	
}
