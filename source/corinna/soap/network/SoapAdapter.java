package corinna.soap.network;

import javax.bindlet.http.HttpStatus;
import javax.bindlet.soap.ISoapBindletRequest;
import javax.bindlet.soap.ISoapBindletResponse;
import javax.bindlet.soap.ISoapMarshaler;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import corinna.exception.AdapterException;
import corinna.network.Adapter;
import corinna.network.IAdapterConfig;
import corinna.network.RequestEvent;
import corinna.soap.bindlet.SoapBindletRequest;
import corinna.soap.bindlet.SoapBindletResponse;


public class SoapAdapter extends Adapter
{

	private SoapUnmarshaller unmarshaller;

	private ISoapMarshaler marshaller;
	
	public SoapAdapter( IAdapterConfig config ) throws SOAPException
	{
		super(config);
		
		unmarshaller = new SoapUnmarshaller(SOAPConstants.SOAP_1_1_PROTOCOL);
		marshaller = new SoapMarshaller(SOAPConstants.SOAP_1_1_PROTOCOL);
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
		
		try
		{
			ISoapBindletRequest r = new SoapBindletRequest(req, unmarshaller);
			ISoapBindletResponse p = new SoapBindletResponse(marshaller, res, channel);
			
			return new SoapRequestEvent(r, p);
		} catch (Exception e)
		{
			throw new AdapterException("Error translating HTTP message to SOAP", e);
		}		
	}

	@Override
	public Class<?> getOutputResponseType()
	{
		return ISoapBindletResponse.class;
	}

	@Override
	public Class<?> getOutputRequestType()
	{
		return ISoapBindletRequest.class;
	}

	/*@Override
	public boolean evaluate( Object request, Object response )
	{
		boolean valid;
		
		valid  = HttpRequest.class.isAssignableFrom(request.getClass());
		valid &= HttpResponse.class.isAssignableFrom(response.getClass());
		if (!valid) return false;
		
		String value = ((HttpRequest)request).getHeader(HttpHeaders.Names.CONTENT_TYPE);

		return (value != null && value.contains("text/xml")) || 
		       ((HttpRequest)request).getUri().endsWith("?wsdl");
	}*/

	@Override
	public void onError( RequestEvent<?, ?> event, Channel channel, Throwable exception )
	{
		try
		{
			ISoapBindletResponse response = (ISoapBindletResponse) event.getResponse();
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
		} catch (Exception e)
		{
			// supress any error
		}
	}

	@Override
	public void onSuccess( RequestEvent<?, ?> event, Channel channel )
	{
		try
		{
			ISoapBindletResponse response = (ISoapBindletResponse) event.getResponse();
			
			if (!event.isHandled())
				response.sendError(HttpStatus.NOT_FOUND);
			else
				response.close();
			channel.close();
		} catch (Exception e)
		{
			// supress any error
		}
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
