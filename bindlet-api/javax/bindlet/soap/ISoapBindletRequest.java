package javax.bindlet.soap;

import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IWebBindletRequest;
import javax.xml.soap.SOAPMessage;


public interface ISoapBindletRequest extends IWebBindletRequest
{

	public SOAPMessage getMessage();
	
}
