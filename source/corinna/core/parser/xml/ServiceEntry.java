package corinna.core.parser.xml;

import java.util.LinkedList;
import java.util.List;

import corinna.core.IBasicConfig;


public class ServiceEntry extends BasicEntry
{

	private List<String> contexts;
	
	public ServiceEntry( String className, IBasicConfig config )
	{
		super(className, config);
		contexts = new LinkedList<String>();
	}
	
	public void addContext( String name )
	{
		contexts.add(name);
	}
	
	public List<String> getContexts()
	{
		return contexts;
	}
	
}
