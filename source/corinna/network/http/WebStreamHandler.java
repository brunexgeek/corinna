package corinna.network.http;

import javax.bindlet.http.IWebBindletRequest;
import javax.bindlet.http.IWebBindletResponse;
import javax.bindlet.soap.ISoapBindletRequest;
import javax.xml.soap.SOAPConstants;

import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;

import corinna.bindlet.http.HttpBindletRequest;
import corinna.bindlet.http.HttpBindletResponse;
import corinna.bindlet.soap.SoapBindletRequest;
import corinna.bindlet.soap.SoapBindletResponse;
import corinna.network.soap.SoapMarshaller;
import corinna.network.soap.SoapUnmarshaller;
import corinna.util.StateModel;
import corinna.util.StateModel.Model;

@StateModel(Model.STATELESS)
public class WebStreamHandler extends BasicStreamHandler
{

	private SoapUnmarshaller unmarshaller = null;

	private SoapMarshaller marshaller = null;
	
	public WebStreamHandler( WebNetworkConnector connector )
	{
		super(connector);
		try
		{
			unmarshaller = new SoapUnmarshaller(SOAPConstants.SOAP_1_1_PROTOCOL);
			marshaller = new SoapMarshaller(SOAPConstants.SOAP_1_1_PROTOCOL);
		} catch (Exception e)
		{
			// supress any errors
		}
	}

	@Override
	protected IWebBindletRequest createRequest( MessageEvent event )
	{
		if (!(event.getMessage() instanceof HttpRequest)) return null;
		HttpRequest request = (HttpRequest)event.getMessage();
		
		// check if the current message is a SOAP request
		if (isSoap(request))
		{
			try
			{
				if (unmarshaller != null) return new SoapBindletRequest(request, unmarshaller);
			} catch (Exception e)
			{
				// supress any errors
				e.printStackTrace();
			}
			return null;
		}
		else
			return new HttpBindletRequest(request);
	}

	@Override
	protected IWebBindletResponse createResponse( MessageEvent event )
	{
		if (!(event.getMessage() instanceof HttpRequest)) return null;
		HttpRequest request = (HttpRequest)event.getMessage();
		
		// check if the current message is a SOAP request
		if (isSoap(request))
		{
			try
			{
				if (unmarshaller != null) return new SoapBindletResponse(event.getChannel(), 
					marshaller, request.getProtocolVersion());
			} catch (Exception e)
			{
				// supress any errors
			}
			return null;
		}
		else
			return new HttpBindletResponse(event.getChannel(), request.getProtocolVersion() );
	}

	protected boolean isSoap( HttpRequest request )
	{
		String contentType = request.getHeader(HttpHeaders.Names.ACCEPT);
		boolean wsdl = request.getUri().endsWith(ISoapBindletRequest.URI_WSDL);
		return (contentType != null && contentType.contains("soap+xml")) || wsdl;
	}
	
}
