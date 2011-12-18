package corinna.core.parser.xml;

import java.util.LinkedList;
import java.util.List;


public class ServiceEntry extends BasicEntry
{

	private List<String> contexts;
	
	public ServiceEntry( String name, String className )
	{
		super(name, className);
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
