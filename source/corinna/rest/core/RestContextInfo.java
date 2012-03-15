package corinna.rest.core;

import javax.bindlet.IComponentInformation;

import corinna.http.core.HttpContextInfo;


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
