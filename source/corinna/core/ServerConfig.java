package corinna.core;


public class ServerConfig extends BasicConfig implements IServerConfig
{

	public ServerConfig( String name )
	{
		super(name);
	}

	@Override
	public String getServerName()
	{
		return name;
	}
	
}
