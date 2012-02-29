//package corinna.network.soap;
//
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.Charset;
//
//import javax.xml.soap.MessageFactory;
//import javax.xml.soap.SOAPException;
//import javax.xml.soap.SOAPMessage;
//
//
//public class SoapUtils
//{
//
//	private static MessageFactory factory = null;
//
//	private static String soapProtocol = javax.xml.soap.SOAPConstants.SOAP_1_1_PROTOCOL;
//	
//	static
//	{
//		try
//		{
//			factory = MessageFactory.newInstance(soapProtocol);
//		} catch (SOAPException e)
//		{
//			// supress any errors
//		}
//	}
//
//	public String marshall( SOAPMessage message ) throws SOAPException, IOException
//	{
//		if (message == null) return null;
//		ByteArrayOutputStream output = new ByteArrayOutputStream();
//		message.writeTo(output);
//		return output.toString();
//	}
//
//	public SOAPMessage unmarshall( String message, Charset charset )
//		throws SOAPException, IOException
//	{
//		ByteArrayInputStream input = new ByteArrayInputStream(message.getBytes(charset));
//		return unmarshall(input);
//	}
//
//	public SOAPMessage unmarshall( InputStream message ) throws SOAPException, IOException
//	{
//		if (factory == null || message == null || message.available() <= 0) return null;
//		return factory.createMessage(null, message);
//	}
//	
//}
