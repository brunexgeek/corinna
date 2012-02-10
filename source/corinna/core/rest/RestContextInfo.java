package corinna.core.rest;

import corinna.core.http.HttpContextInfo;
import corinna.util.IComponentInformation;


public class RestContextInfo implements IComponentInformation
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
		return "REST Context";
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
