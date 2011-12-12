package corinna.bindlet.soap;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;

import javax.bindlet.http.Cookie;
import javax.bindlet.http.ISession;
import javax.bindlet.soap.ISoapBindletRequest;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;

import corinna.bindlet.http.WebBindletRequest;
import corinna.network.soap.SoapUnmarshaller;


public class SoapBindletRequest extends WebBindletRequest implements ISoapBindletRequest
{
	
	private Charset charset = null;
	
	private String content = null;
	
	private SOAPMessage message = null;

	private HttpRequest request;
	
	public SoapBindletRequest( HttpRequest request, SoapUnmarshaller unmarshaller ) 
		throws SOAPException, IOException
	{
		super(request);
		this.request = request;
		
		// get the content charset
		String value = request.getHeader(HttpHeaders.Names.CONTENT_ENCODING);
		if (value == null)
			charset = Charset.defaultCharset();
		else
			charset = Charset.forName(value);
		
		// get the content
		if ( request.getContent() == null)
			content = "";
		else
		{
			content = request.getContent().toString(charset);
			message = unmarshaller.unmarshall(content, charset);
		}
		
		// check if the client 
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
