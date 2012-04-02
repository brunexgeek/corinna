package corinna.bean;


public abstract class ServiceBean implements IServiceBean
{
	
	private IBeanConfig config;
	
	public ServiceBean( IBeanConfig config )
	{
		if (config == null)
			throw new NullPointerException("The bean configuration can not be null");
		this.config = config;
	}
	
	@Override
	public String getName()
	{
		return config.getBeanName();
	}

	@Override
	public IBeanConfig getConfig()
	{
		return config;
	}
	
}
