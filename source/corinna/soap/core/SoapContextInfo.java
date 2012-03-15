package corinna.soap.core;

import javax.bindlet.IComponentInformation;


public class SoapContextInfo  implements IComponentInformation
{

	private static IComponentInformation instance = null;
	
	public static IComponentInformation getInstance()
	{
		if (instance == null) instance = new SoapContextInfo();
		return instance;
	}
	
	@Override
	public String getComponentName()
	{
		return "SOAP Context";
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
