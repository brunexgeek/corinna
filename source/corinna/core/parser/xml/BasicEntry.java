package corinna.core.parser.xml;


public class BasicEntry
{


	private String name;

	private String className;

	public BasicEntry( String name, String className )
	{
		this.name = name;
		this.className = className;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setClassName( String className )
	{
		this.className = className;
	}

	public String getClassName()
	{
		return className;
	}
	
}
