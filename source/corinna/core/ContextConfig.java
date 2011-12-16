package corinna.core;

import java.util.Map;


public final class ContextConfig implements IContextConfig
{

	private Map<String,String> params;
	
	private String[] paramsArray = null;
	
	private String name;
	
	private Class<?> classRef;
	
	public ContextConfig( String name, String className ) throws ClassNotFoundException
	{
		this(name, Class.forName(className));
	}
	
	public ContextConfig( String name, Class<?> classRef )
	{
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("The context name can not be null or empty");
		if (classRef == null)
			throw new IllegalArgumentException("The context class can not be null");
		
		this.name = name;
		this.classRef = classRef;
	}

	@Override
	public String getContextName()
	{
		return name;
	}
	
	@Override
	public String getInitParameter( String name )
	{
		return params.get(name);
	}
	
	public void setInitParameter( String name, String value )
	{
		if (value == null || name == null) return;
		params.put(name, value);
		paramsArray = null;
	}
	
	@Override
	public String[] getInitParameterNames()
	{
		if (paramsArray == null)
			paramsArray = params.keySet().toArray(new String[0]);
		return paramsArray;
	}
	
	@Override
	public Class<?> getContextClass()
	{
		return classRef;
	}
	
}
