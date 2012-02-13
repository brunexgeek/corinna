package corinna.core.parser.xml;

import java.util.LinkedList;
import java.util.List;

import corinna.util.conf.ISection;


public class ContextEntry extends BindletEntry
{

	private List<String> bindlets;
	
	public ContextEntry( String name, String className, ISection config )
	{
		super(name, className, config);
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
