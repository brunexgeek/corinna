package corinna.core.parser.xml;

import java.util.LinkedList;
import java.util.List;


public class ContextEntry extends BindletEntry
{

	private List<String> bindlets;
	
	public ContextEntry( String name, String className )
	{
		super(name, className);
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
