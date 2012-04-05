package corinna.rest.network;

import javax.bindlet.http.HttpStatus;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import corinna.exception.AdapterException;
import corinna.network.Adapter;
import corinna.network.IAdapterConfig;
import corinna.network.RequestEvent;
import corinna.rest.bindlet.IRestBindletRequest;
import corinna.rest.bindlet.IRestBindletResponse;
import corinna.rest.bindlet.RestBindletRequest;
import corinna.rest.bindlet.RestBindletResponse;


public class RestAdapter extends Adapter
{

	public RestAdapter( IAdapterConfig config )
	{
		super(config);
	}

	@Override
	public Class<?> getOutputResponseType()
	{
		return IRestBindletRequest.class;
	}

	@Override
	public Class<?> getOutputRequestType()
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

	/*@Override
	public boolean evaluate( Object request, Object response )
	{
		boolean valid;
		
		valid  = HttpRequest.class.isAssignableFrom(request.getClass());
		valid &= HttpResponse.class.isAssignableFrom(response.getClass());
		if (!valid) return false;
		
		String value = ((HttpRequest)request).getHeader(HttpHeaders.Names.CONTENT_TYPE);

		return (value != null && value.contains("text/html"));
	}*/

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

	@Override
	public Class<?> getInputRequestType()
	{
		return HttpRequest.class;
	}

	@Override
	public Class<?> getInputResponseType()
	{
		return HttpResponse.class;
	}
	
}
