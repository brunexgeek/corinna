package corinna.core.parser.xml;


public class ConnectorEntry extends BasicEntry
{

	private String address;

	public ConnectorEntry( String name, String className, String address )
	{
		super(name, className);
		this.address = address;
	}

	public String getAddress()
	{
		return address;
	}

}
