package corinna.network;


public abstract class Adapter<R,P> implements IAdapter<R,P>
{

	private IAdapterConfig config;
	
	public Adapter( IAdapterConfig config )
	{
		if (config == null)
			throw new IllegalArgumentException("The adapter configuration object can not be null");
		
		this.config = config;
	}
	
	public IAdapterConfig getConfig()
	{
		return config;
	}
	
	@Override
	public String getName()
	{
		return config.getAdapterName();
	}
}
