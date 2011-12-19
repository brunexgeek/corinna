package corinna.network.web;

import javax.bindlet.http.IWebBindletRequest;
import javax.bindlet.http.IWebBindletResponse;

import corinna.network.Protocol;


public class WebProtocol extends Protocol<IWebBindletRequest, IWebBindletResponse>
{

	private static final String PROTOCOL_SCHEME = "http";

	private static final String PROTOCOL_VERSION = "1.1";

	private static final String PROTOCOL_NAME = "Unified HTTP";

	private static final String PROTOCOL_IMPLEMENTOR = "Bruno Ribeiro and Netty Project";

	private static final String PROTOCOL_LICENSE = "Apache License";
	
	private static WebProtocol instance = null;

	private WebProtocol()
	{
	}
	
	public static WebProtocol getInstance()
	{
		if (instance == null)
			instance = new WebProtocol();
		return instance;
	}
	
	@Override
	public String getScheme()
	{
		return PROTOCOL_SCHEME;
	}

	@Override
	public String getVersion()
	{
		return PROTOCOL_VERSION;
	}

	@Override
	public String getName()
	{
		return PROTOCOL_NAME;
	}
	
	@Override
	public String getImplementor()
	{
		return PROTOCOL_IMPLEMENTOR;
	}

	@Override
	public String getDescription()
	{
		return "";
	}

	@Override
	public String getLicense()
	{
		return PROTOCOL_LICENSE;
	}

}
