package corinna.core.parser.xml;

import java.util.HashMap;
import java.util.Map;


public class BindletEntry extends BasicEntry
{

	private Map<String,String> params;
	
	public BindletEntry( String name, String className )
	{
		super(name, className);
		params = new HashMap<String,String>();
	}

	public void setParameter( String name, String value )
	{
		params.put(name, value);
	}
	
	public String getParameter( String name )
	{
		if (name == null) return null;
		return params.get(name);
	}
	
	public String[] getParameterNames()
	{
		return params.keySet().toArray(new String[0]);
	}
	
}
