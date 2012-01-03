package corinna.bindlet.soap;

import javax.bindlet.soap.ISoapBindletResponse;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import corinna.bindlet.http.WebBindletResponse;
import corinna.network.soap.SoapMarshaller;


public class SoapBindletResponse extends WebBindletResponse implements ISoapBindletResponse
{
	
	private Exception exception = null;
	
	private String wsdl = null;
	
	private SOAPMessage message = null;
	
	private SoapMarshaller marshaller = null;
	
	public SoapBindletResponse( Channel channel, SoapMarshaller marshaller, HttpVersion version ) 
		throws SOAPException
	{
		super(channel, new DefaultHttpResponse(version, HttpResponseStatus.OK));
		this.marshaller = marshaller;
		this.message = SoapUtils.createMessage();
	}

	@Override
	public Exception getException()
	{
		return exception;
	}

	@Override
	public SoapMarshaller getMarshaller()
	{
		return marshaller;
	}

	public SOAPMessage getMessage()
	{
		return message;
	}

	public String getWsdl()
	{
		return wsdl;
	}

	public void setException( Exception ex )
	{
		exception = ex;
	}

	public void setMessage( SOAPMessage message )
	{
		this.message = message;
	}

	public void setWsdl( String wsdl )
	{
		this.wsdl = wsdl;
	}
	
}
