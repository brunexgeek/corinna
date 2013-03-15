package corinna.rpc;

import java.lang.reflect.Method;


public class POJOInfo
{
	
	private String suffix;
	private Method getter;
	private Method setter;
	private Class<?> type;

	protected POJOInfo( String suffix, Class<?> type, Method getter, Method setter )
	{
		this.suffix = suffix;
		this.getter = getter;
		this.setter = setter;
		this.type = type;
	}

	public String getSuffix()
	{
		return suffix;
	}

	public Method getGetter()
	{
		return getter;
	}

	public Method getSetter()
	{
		return setter;
	}

	public Class<?> getType()
	{
		return type;
	}
}