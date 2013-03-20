package javax.bindlet.http;


public enum HttpMethod
{

	GET("GET"),

	POST("POST"),

	PUT("PUT"),

	TRACE("TRACE"),

	OPTIONS("OPTIONS"),

	DELETE("DELETE"),

	HEAD("HEAD");

	private String name;

	private HttpMethod( String name )
	{
		if (name == null) throw new NullPointerException("HTTP method name can not be null");
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public HttpMethod valueOf( String text, HttpMethod defaultValue )
	{
		try
		{
			return HttpMethod.valueOf(text);
		} catch (Exception e)
		{
			return defaultValue;
		}
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
