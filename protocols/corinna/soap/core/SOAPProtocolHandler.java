package corinna.soap.core;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;

import javax.bindlet.exception.BindletException;
import javax.bindlet.http.HttpStatus;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;
import javax.bindlet.http.io.HttpBindletInputStream;
import javax.bindlet.io.BindletOutputStream;
import javax.bindlet.rpc.IProcedureCall;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import corinna.network.IProtocol;
import corinna.network.IProtocolHandler;
import corinna.rpc.ProcedureCall;
import corinna.soap.bindlet.SOAPUtils;
import corinna.soap.network.SOAPProtocol;


public class SOAPProtocolHandler implements
	IProtocolHandler<IHttpBindletRequest, IHttpBindletResponse>
{

	private static Logger log = LoggerFactory.getLogger(SOAPProtocolHandler.class);

	protected String localXMLSchemaNamespace = null;
	
	protected MessageFactory messageFectory;

	public SOAPProtocolHandler( String namespace ) throws SOAPException
	{
		if (namespace == null)
			throw new IllegalArgumentException("The local XML schema namespace can not be null");
		
		localXMLSchemaNamespace = namespace;
		// create a factory to create SOAP messages 
		messageFectory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
	}
	
	protected Charset getCharsetByName( String encoding )
	{
		try
		{
			return Charset.forName(encoding.toUpperCase());
		} catch (Exception e)
		{
			return Charset.defaultCharset();
		}
	}

	protected SOAPMessage getMessage( IHttpBindletRequest request )
	{
		Charset charset = getCharsetByName(request.getCharacterEncoding());
		try
		{
			HttpBindletInputStream input = (HttpBindletInputStream) request.getInputStream();
			String content = input.readText(charset);
			input.close();
			return unmarshall(content, charset);
		} catch (Exception e)
		{
			// sumprime os erros
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ProcedureCall readRequest( IHttpBindletRequest request ) throws BindletException
	{
		SOAPMessage soapMessage = getMessage(request);
		SOAPElement soapRoot = null;

		if (soapMessage == null) return null;

		try
		{
			// get the SOAP element containing the request parameters
			soapRoot = SOAPUtils.getBodyElement(soapMessage.getSOAPBody());

			// extract the method name from SOAP action
			// TODO: ask to the WSDLGenerator about the root element name formation?
			String soapMethodName = soapRoot.getElementQName().getLocalPart();
			if (!soapMethodName.endsWith(WSDLGenerator.SUFFIX_INPUT_MESSAGE)
				|| soapMethodName.length() <= WSDLGenerator.SUFFIX_INPUT_MESSAGE.length())
				throw new BindletException("Unknow SOAP message \"" + soapMethodName + "\"");
			soapMethodName = soapMethodName.substring(0, soapMethodName.length()
				- WSDLGenerator.SUFFIX_INPUT_MESSAGE.length());

			ProcedureCall procedure = new ProcedureCall(soapMethodName);

			// find all procedure parameters
			Iterator it = soapRoot.getChildElements();
			SOAPElement current;
			while ((current = SOAPUtils.getNextSOAPElement(it)) != null)
			{
				Object value;
				SOAPElement param = (SOAPElement) current;

				// verifica se o parâmetro é nulo
				if (param.hasAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "nil"))
					value = null;
				else
				// verifica se o parâmetro é um POJO
				if (SOAPUtils.canParseAsPOJO(param))
					value = SOAPUtils.parseAsPOJO(param);
				else
				// verifica se o parâmetro é um valor simples
				if (SOAPUtils.canParseAsPrimitive(param))
					value = SOAPUtils.parseAsPrimitive(param);
				else
					throw new BindletException("Invalid SOAP parameter value for \""
							+ param.getLocalName() + "\"");
				procedure.setParameter(param.getLocalName(), value);
			}

			return procedure;
		} catch (BindletException e)
		{
			throw e;
		} catch (Exception e)
		{
			throw new BindletException("Error parsing SOAP request", e);
		}
	}

	protected SOAPMessage createSoapResponse( String namespace, String prototype, Object result )
		throws SOAPException
	{
		SOAPMessage message = SOAPUtils.createMessage();

		// create the SOAP method response element
		SOAPBody body = message.getSOAPBody();
		//QName qname = new QName(namespace, prototype + WSDLGenerator.SUFFIX_OUTPUT_MESSAGE, "sxsd");
		QName qname = new QName(prototype + WSDLGenerator.SUFFIX_OUTPUT_MESSAGE);
		SOAPElement element = body.addChildElement(qname);
		// create the return value element
		SOAPUtils.generateElement(element, WSDLGenerator.RETURN_VALUE_NAME, result);

		return message;
	}

	protected SOAPMessage createSoapFault( String namespace, Exception error ) throws SOAPException
	{
		Throwable t = error;
		SOAPMessage message = SOAPUtils.createMessage();

		// find the first error with a message
		while (t != null && t.getCause() != null && t.getCause().getMessage() != null)
		{
			t = t.getCause();
		}
		String text = t.getMessage();
		// detect the error code
		String errorCode = t.getClass().getSimpleName();
		
		log.error("SOAP fault", error);

		// create the SOAP method response element
		SOAPBody body = message.getSOAPBody();
		QName qname = new QName(body.getNamespaceURI(), "fault");
		SOAPElement element = body.addChildElement(qname);
		element.setPrefix(body.getPrefix());
		// create the 'faultcode' element
		qname = new QName(body.getNamespaceURI(), "faultcode");
		SOAPElement sub = element.addChildElement(qname);
		//sub.setValue(body.getPrefix() + ":Server.userException");
		sub.setValue(errorCode);
		// create the 'faultstring' element
		qname = new QName(body.getNamespaceURI(), "faultstring");
		sub = element.addChildElement(qname);
		sub.setValue(text);
		// create the 'detail' element
		qname = new QName(body.getNamespaceURI(), "detail");
		sub = element.addChildElement(qname);
		sub.setValue("");
		
		return message;
	}

	@Override
	public void writeResponse( IHttpBindletResponse response, IProcedureCall procedure,
		Object returnValue ) throws BindletException, IOException
	{
		SOAPMessage resp = null;

		try
		{
			resp = createSoapResponse(getXMLSchemaNamespace(), procedure.getMethodPrototype(),
				returnValue);
		} catch (Exception e)
		{
			writeException(response, e);
			return;
		}

		try
		{
			String content = marshall(resp);
			response.setContentType("text/xml");

			BindletOutputStream out = response.getOutputStream();
			out.writeString(content);
			out.close();
		} catch (Exception e)
		{
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String getXMLSchemaNamespace()
	{
		return localXMLSchemaNamespace;
	}

	@Override
	public void writeException( IHttpBindletResponse response, Exception exception )
		throws BindletException, IOException
	{
		SOAPMessage resp = null;

		try
		{
			resp = createSoapFault("", exception);
		} catch (SOAPException e)
		{
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		try
		{
			String content = marshall(resp);
			response.setContentType("text/xml");
			//response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

			BindletOutputStream out = response.getOutputStream();
			out.writeString(content);
			out.close();
		} catch (Exception e)
		{
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	protected String marshall( SOAPMessage message ) throws SOAPException, IOException
	{
		if (message == null) return null;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		message.writeTo(output);
		return output.toString();
	}

	public SOAPMessage unmarshall( InputStream message ) throws SOAPException, IOException
	{
		if (message == null || message.available() <= 0) return null;
		return messageFectory.createMessage(null, message);
	}

	public SOAPMessage unmarshall( String message, Charset charset ) throws SOAPException, IOException
	{
		ByteArrayInputStream input = new ByteArrayInputStream(message.getBytes(charset));
		return unmarshall(input);
	}

	@Override
	public IProtocol getProtocol()
	{
		return SOAPProtocol.getInstance();
	}
	
}
