package corinna.soap.bindlet;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;

import javax.bindlet.exception.BindletException;
import javax.bindlet.http.HttpStatus;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;
import javax.bindlet.http.io.HttpBindletInputStream;
import javax.bindlet.io.BindletOutputStream;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import corinna.rpc.IProcedureCall;
import corinna.rpc.ProcedureCall;
import corinna.soap.core.WsdlGenerator;
import corinna.soap.network.SoapMarshaller;
import corinna.soap.network.SoapUnmarshaller;


@SuppressWarnings("serial")
public abstract class SoapBindlet extends javax.bindlet.soap.SoapBindlet
{

	private static Logger serverLog = LoggerFactory.getLogger(SoapBindlet.class);
	
	protected static SoapUnmarshaller unmarshaller = null;
	
	protected static SoapMarshaller marshaller = null;
	
	static
	{
		try
		{
			unmarshaller = new SoapUnmarshaller(SOAPConstants.SOAP_1_1_PROTOCOL);
			marshaller = new SoapMarshaller(SOAPConstants.SOAP_1_1_PROTOCOL);
		} catch (Exception e)
		{
			// suprime qualquer erro
		}
	}
	
	public SoapBindlet() throws BindletException
	{
		super();
	}

	protected Charset getCharsetByName( String encoding )
	{
		try
		{
			return Charset.forName( encoding.toUpperCase() );
		} catch (Exception e)
		{
			return Charset.defaultCharset();
		}
	}
	
	protected SOAPMessage getMessage( IHttpBindletRequest request )
	{
		Charset charset = getCharsetByName( request.getCharacterEncoding() );
		try
		{
			HttpBindletInputStream input = (HttpBindletInputStream)request.getInputStream();
			String content = input.readText(charset);
			input.close();
			return unmarshaller.unmarshall(content, charset);
		} catch (Exception e)
		{
			// sumprime os erros
			return null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected ProcedureCall getProcedureCall( IHttpBindletRequest request, IHttpBindletResponse response )
	{
		SOAPMessage soapMessage = getMessage(request);
		SOAPElement soapMethod = null;
		
		if (soapMessage == null) return null;
		
		try
		{
			SOAPBody body = soapMessage.getSOAPBody();
			Iterator it = body.getChildElements();
			while (it.hasNext())
			{
				Object current = it.next();
				if (current instanceof SOAPElement)
				{
					soapMethod = (SOAPElement)current;
					break;
				}
			}
			if (soapMethod == null) return null;

			// extract the method name from input parameters
			String soapMethodName = soapMethod.getElementQName().getLocalPart();
			int pos = soapMethodName.indexOf("InputType");
			if (pos <= 0) return null;
			soapMethodName = soapMethodName.substring(0, pos);
			
			ProcedureCall procedure = new ProcedureCall(soapMethodName);
			
			// find all procedure parameters
			it = soapMethod.getChildElements();
			while (it.hasNext())
			{
				Object current = it.next();
				if (current instanceof SOAPElement)
				{
					SOAPElement param = (SOAPElement)current;
					
					procedure.setParameter(param.getLocalName(), getElementContent(param));
				}
			}
			
			procedure.setParameter(PARAM_REQUEST, request);
			procedure.setParameter(PARAM_RESPONSE, response);
			
			return procedure;
		} catch (Exception e)
		{
			serverLog.error("Error parsing SOAP message", e);
			return null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected String getElementContent( SOAPElement element )
	{
		// find all procedure parameters
		Iterator it = element.getChildElements();
		while (it.hasNext())
		{
			Object current = it.next();
			if (current instanceof Text)
			{
				Text content = (Text)current;
				return content.getData();
			}
		}
		return null;
	}

	protected SOAPMessage createSoapResponse( String namespace, String prototype, Object result ) throws SOAPException
	{
		SOAPMessage message = SoapUtils.createMessage();
		
		// create the SOAP method response element
		SOAPBody body = message.getSOAPBody();
		QName qname = new QName(prototype + "OutputType");
		SOAPElement element = body.addChildElement(qname);
		// create the return value element
		qname = new QName(WsdlGenerator.PARAMETER_RESULT);
		SOAPElement carrier = element.addChildElement(qname);
		carrier.setValue( (result == null) ? "" : result.toString() );
		
		return message;
	}

	
	protected void doPost( IHttpBindletRequest request, IHttpBindletResponse response )
	throws BindletException, IOException
	{
		// extract the SOAP message from request content
		IProcedureCall call = getProcedureCall(request, response);
		Object result = doCall(call);
		
		try
		{
			SOAPMessage resp = createSoapResponse("", call.getMethodPrototype(), result);
			String content = marshaller.marshall(resp);
			
			response.setContentType("text/xml");
			
			BindletOutputStream out = response.getOutputStream();
			out.writeString(content);
			out.close();
		} catch (Exception e)
		{
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
