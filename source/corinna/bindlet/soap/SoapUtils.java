package corinna.bindlet.soap;

import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;
import javax.bindlet.http.IWebBindletRequest;
import javax.bindlet.http.IWebBindletResponse;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import corinna.network.RequestEvent;


public class SoapUtils
{

	private static MessageFactory factory = null;

	public static MessageFactory getMessageFactory() throws SOAPException
	{
		if (factory == null)
			factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
		return factory;
	}
	
	public static SOAPMessage createMessage() throws SOAPException
	{
		return getMessageFactory().createMessage();
	}
	
}
