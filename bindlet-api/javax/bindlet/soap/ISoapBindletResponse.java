package javax.bindlet.soap;

import javax.bindlet.http.IWebBindletResponse;
import javax.xml.soap.SOAPMessage;

import corinna.network.soap.SoapMarshaller;


public interface ISoapBindletResponse extends IWebBindletResponse
{
	
	public void setException( Exception ex );
	
	public Exception getException();
	
	void setWsdl( String wsdl );
	
	String getWsdl();
	
	void setMessage( SOAPMessage message );
	
	SOAPMessage getMessage();
	
	SoapMarshaller getMarshaller();
	
}
