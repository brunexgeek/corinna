package corinna.core.parser.xml;

import java.util.LinkedList;
import java.util.List;

import corinna.core.IContextConfig;


public class ContextEntry extends BasicEntry
{

	private List<String> bindlets;
	
	public ContextEntry( String className, IContextConfig config )
	{
		super(className, config);
		bindlets = new LinkedList<String>();
	}

	public void addBindlet( String name )
	{
		bindlets.add(name);
	}
	
	public List<String> getBindlets()
	{
		return bindlets;
	}
	
}
