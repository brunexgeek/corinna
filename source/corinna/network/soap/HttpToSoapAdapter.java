package corinna.network.soap;

import javax.bindlet.soap.ISoapBindletRequest;
import javax.bindlet.soap.ISoapBindletResponse;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import corinna.bindlet.soap.SoapBindletRequest;
import corinna.bindlet.soap.SoapBindletResponse;
import corinna.exception.AdapterException;
import corinna.network.Adapter;
import corinna.network.AdapterConfig;
import corinna.network.IAdapterConfig;
import corinna.network.RequestEvent;


public class HttpToSoapAdapter extends Adapter<HttpRequest,HttpResponse>
{

	private SoapUnmarshaller unmarshaller;

	private SoapMarshaller marshaller;
	
	public HttpToSoapAdapter( IAdapterConfig config ) throws SOAPException
	{
		super(config);
		
		unmarshaller = new SoapUnmarshaller(SOAPConstants.SOAP_1_1_PROTOCOL);
		marshaller = new SoapMarshaller(SOAPConstants.SOAP_1_1_PROTOCOL);
	}
	
	@Override
	public RequestEvent<?, ?> translate( HttpRequest request, HttpResponse response, Channel channel )
		throws AdapterException
	{
		try
		{
			ISoapBindletRequest req = new SoapBindletRequest(request, unmarshaller);
			ISoapBindletResponse res = new SoapBindletResponse(marshaller, response, channel);
			
			return new SoapRequestEvent(req, res);
		} catch (Exception e)
		{
			throw new AdapterException("Error translating HTTP message to SOAP", e);
		}		
	}

	@Override
	public Class<?> getResponseType()
	{
		return ISoapBindletResponse.class;
	}

	@Override
	public Class<?> getRequestType()
	{
		return ISoapBindletRequest.class;
	}

	@Override
	public boolean isCompatibleWith( HttpRequest request, HttpResponse response )
	{
		String value = request.getHeader(HttpHeaders.Names.CONTENT_TYPE);
		return value.contains("text/xml");
	}

}
