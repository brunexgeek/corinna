package corinna.network;

import corinna.core.BasicConfig;


public class AdapterConfig extends BasicConfig implements IAdapterConfig
{

	public AdapterConfig( String name )
	{
		super(name);
	}
	
	public String getAdapterName()
	{
		return name;
	}
	
}
