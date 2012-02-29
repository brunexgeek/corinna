package corinna.network.rest;

import javax.bindlet.http.HttpStatus;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import corinna.bindlet.rest.IRestBindletRequest;
import corinna.bindlet.rest.IRestBindletResponse;
import corinna.bindlet.rest.RestBindletRequest;
import corinna.bindlet.rest.RestBindletResponse;
import corinna.exception.AdapterException;
import corinna.network.Adapter;
import corinna.network.IAdapterConfig;
import corinna.network.RequestEvent;


public class RestAdapter extends Adapter
{

	public RestAdapter( IAdapterConfig config )
	{
		super(config);
	}

	@Override
	public Class<?> getResponseType()
	{
		return IRestBindletRequest.class;
	}

	@Override
	public Class<?> getRequestType()
	{
		return IRestBindletResponse.class;
	}

	@Override
	public void onError( RequestEvent<?,?> event, Channel channel, Throwable exception )
	{
		try
		{
			IRestBindletResponse response = (IRestBindletResponse) event.getResponse();
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
		} catch (Exception e)
		{
			// supress any error
		}
	}

	@Override
	public void onSuccess( RequestEvent<?,?> event, Channel channel )
	{
		try
		{
			IRestBindletResponse response = (IRestBindletResponse) event.getResponse();
			IRestBindletRequest request = (IRestBindletRequest) event.getRequest();
			
			if (!event.isHandled())
				response.sendError(HttpStatus.NOT_FOUND);
			else
				response.close();
			// close the connection, if necessary
			if (!request.isKeepAlive()) channel.close();
		} catch (Exception e)
		{
			// supress any error
		}
	}

	@Override
	public boolean isCompatibleWith( Object request, Object response )
	{
		return HttpRequest.class.isAssignableFrom(request.getClass()) &&
		       HttpResponse.class.isAssignableFrom(response.getClass());
	}

	@Override
	public RequestEvent<?, ?> translate( Object request, Object response, Channel channel )
		throws AdapterException
	{
		if (request == null)
			throw new NullPointerException("The request object can not be null");
		
		HttpRequest req = (HttpRequest)request;
		HttpResponse res = null;
		
		if (response == null)
			res = new DefaultHttpResponse( req.getProtocolVersion(), HttpResponseStatus.OK);
		else
			res = (HttpResponse)response;

		IRestBindletRequest r = new RestBindletRequest(req);
		IRestBindletResponse p = new RestBindletResponse(channel, res);
		return new RestRequestEvent(r, p);
	}

}
