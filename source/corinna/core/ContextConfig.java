package corinna.core;



public class ContextConfig extends BasicConfig implements IContextConfig
{
	
	public ContextConfig( String name )
	{
		super(name);
	}

	@Override
	public String getContextName()
	{
		return name;
	}
	
}
