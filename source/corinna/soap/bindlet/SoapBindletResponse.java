package corinna.soap.bindlet;

import java.io.IOException;

import javax.bindlet.BindletOutputStream;
import javax.bindlet.soap.ISoapBindletResponse;
import javax.bindlet.soap.ISoapMarshaler;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import corinna.http.bindlet.WebBindletResponse;


public class SoapBindletResponse extends WebBindletResponse implements ISoapBindletResponse
{
	
	private Exception exception = null;
	
	private String wsdl = null;
	
	private SOAPMessage message = null;
	
	private ISoapMarshaler marshaller = null;
	
	public SoapBindletResponse( ISoapMarshaler marshaller, HttpResponse response, Channel channel ) 
	throws SOAPException
	{
		super(channel, response);
		this.marshaller = marshaller;
		this.message = SoapUtils.createMessage();
	}
	
	public SoapBindletResponse( ISoapMarshaler marshaller, HttpVersion version, Channel channel ) 
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
	public ISoapMarshaler getMarshaller()
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
	
	@Override
	public void close() throws IOException
	{
		if (isClosed()) return;

		String messageString = "";
		
		try
		{
			if (exception != null)
			{
				SOAPFault fault = message.getSOAPBody().addFault();
				fault.setFaultCode("SOAP-ENV:Client");
				fault.setFaultString(exception.getMessage());
			}
			messageString = getMarshaller().marshall(message);
		} catch (Exception e)
		{
			// suprime os erros
		}

		if (!isChunked()) setContentLength(messageString.length());
		
		BindletOutputStream out = getOutputStream();
		try
		{
			if (!out.isClosed() && out.writtenBytes() == 0)
			{
				out.write(messageString);
			}
		} catch (Exception e)
		{
			// suprime os erros
		}
		if (out != null && !out.isClosed()) out.close();
	}
	
}
