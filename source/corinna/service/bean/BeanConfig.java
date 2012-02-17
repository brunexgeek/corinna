package corinna.service.bean;

import corinna.core.BasicConfig;


public class BeanConfig extends BasicConfig implements IBeanConfig
{
	
	public BeanConfig( String name )
	{
		super(name);
	}

	@Override
	public String getBeanName()
	{
		return name;
	}

}
