package corinna.bindlet.soap;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.bindlet.soap.ISoapBindletRequest;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpRequest;

import corinna.bindlet.http.WebBindletRequest;
import corinna.network.soap.SoapUnmarshaller;


public class SoapBindletRequest extends WebBindletRequest implements ISoapBindletRequest
{
	
	private static Logger log = Logger.getLogger(SoapBindletRequest.class);
	
	private Charset charset = null;
	
	private String content = null;
	
	private SOAPMessage message = null;

	private HttpRequest request;
	
	public SoapBindletRequest( HttpRequest request, SoapUnmarshaller unmarshaller ) 
		throws SOAPException, IOException
	{
		super(request);
		this.request = request;

		String encoding = getCharacterEncoding();
		if (encoding != null && !encoding.isEmpty()) charset = Charset.forName(encoding);
		
		// get the content
		if ( request.getContent() == null || request.getUri().endsWith(ISoapBindletRequest.URI_WSDL))
			content = "";
		else
		{
			content = request.getContent().toString(charset);
			message = unmarshaller.unmarshall(content, charset);
		}
	}

	@Override
	public SOAPMessage getMessage()
	{
		return message;
	}

	@Override
	public String getRequestURI()
	{
		return request.getUri();
	}
	
}
