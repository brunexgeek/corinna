package corinna.core;


public class ServiceConfig extends BasicConfig implements IServiceConfig
{

	public ServiceConfig( String name )
	{
		super(name);
	}

	@Override
	public String getServiceName()
	{
		return name;
	}

}
