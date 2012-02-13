package corinna.core.parser.xml;

import corinna.util.conf.ISection;


public class ConnectorEntry extends BindletEntry
{

	private String address;

	public ConnectorEntry( String name, String className, String address, ISection config )
	{
		super(name, className, config);
		this.address = address;
	}

	public String getAddress()
	{
		return address;
	}

}
