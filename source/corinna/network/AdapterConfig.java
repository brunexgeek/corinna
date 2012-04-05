package corinna.network;

import java.util.LinkedList;
import java.util.List;

import corinna.core.BasicConfig;
import corinna.core.parser.xml.IFilterContainer;


public class AdapterConfig extends BasicConfig implements IAdapterConfig, IFilterContainer
{

	private List<String> filters;
	
	public AdapterConfig( String name )
	{
		super(name);
		filters = new LinkedList<String>();
	}
	
	public String getAdapterName()
	{
		return name;
	}

	@Override
	public void addFilter( String className )
	{
		if (className == null || className.isEmpty()) return;
		filters.add(className);
	}

	@Override
	public List<String> getFilters()
	{
		return filters;
	}
	
}
