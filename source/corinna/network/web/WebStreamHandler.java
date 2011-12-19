package corinna.network.web;

import javax.bindlet.http.IWebBindletRequest;
import javax.bindlet.http.IWebBindletResponse;

import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpRequest;

import corinna.bindlet.http.HttpBindletRequest;
import corinna.bindlet.http.HttpBindletResponse;


public class WebStreamHandler extends BasicStreamHandler
{

	public WebStreamHandler( WebNetworkConnector connector )
	{
		super(connector);
	}

	@Override
	protected IWebBindletRequest createRequest( MessageEvent event )
	{
		return new HttpBindletRequest((HttpRequest)event.getMessage());
	}

	@Override
	protected IWebBindletResponse createResponse( MessageEvent event )
	{
		HttpRequest req = (HttpRequest)event.getMessage();
		return new HttpBindletResponse(event.getChannel(), req.getProtocolVersion() );
	}

}
