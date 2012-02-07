package corinna.service.bean;

import java.util.HashMap;
import java.util.Map;


public class BeanConfig implements IBeanConfig
{
	
	private Map<String,String> params;
	
	private String[] paramsArray = null;

	private String beanName;
	
	public BeanConfig( String beanName )
	{
		if (beanName == null || beanName.isEmpty())
			throw new IllegalArgumentException("The bean name can not be null or empty");
		
		params = new HashMap<String, String>();
		this.beanName = beanName;
		
	}
	
	public void setBeanParameter( String name, String value )
	{
		if (name == null) return;
		if (value == null) removeBeanParameter(name);
		
		synchronized (params)
		{
			params.put(name, value);
		}
	}
	
	public String removeBeanParameter( String name )
	{
		synchronized (params)
		{
			return params.remove(name);
		}
	}
	
	@Override
	public String getBeanName()
	{
		return beanName;
	}

	@Override
	public String getBeanParameter( String name )
	{
		synchronized (params)
		{
			return params.get(name);
		}
	}

	@Override
	public String[] getBeanParameterNames()
	{
		synchronized (paramsArray)
		{
			if (paramsArray == null)
				synchronized (params)
				{
					paramsArray = params.keySet().toArray( new String[0] );
				}
			return paramsArray;
		}
	}

}
