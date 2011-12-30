package corinna.core.http;

import corinna.util.IComponentInformation;


public class HttpContextInfo  implements IComponentInformation
{

	private static IComponentInformation instance = null;
	
	public static IComponentInformation getInstance()
	{
		if (instance == null) instance = new HttpContextInfo();
		return instance;
	}
	
	@Override
	public String getComponentName()
	{
		return "HTTP Context";
	}

	@Override
	public String getComponentVersion()
	{
		return "1.0";
	}

	@Override
	public String getComponentImplementor()
	{
		return "Bruno Ribeiro";
	}
	
}