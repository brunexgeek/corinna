package corinna.core.parser.xml;

import java.util.LinkedList;
import java.util.List;

import corinna.util.conf.ISection;


public class ServiceEntry extends BindletEntry
{

	private List<String> contexts;
	
	public ServiceEntry( String name, String className, ISection config )
	{
		super(name, className, config);
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
