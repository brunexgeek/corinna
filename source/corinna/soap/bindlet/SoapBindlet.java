package corinna.soap.bindlet;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;

import javax.bindlet.BindletModel;
import javax.bindlet.BindletModel.Model;
import javax.bindlet.exception.BindletException;
import javax.bindlet.http.HttpStatus;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;
import javax.bindlet.http.io.HttpBindletInputStream;
import javax.bindlet.io.BindletOutputStream;
import javax.bindlet.rpc.IProcedureCall;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import corinna.rpc.BeanObject;
import corinna.rpc.POJOUtil;
import corinna.rpc.ProcedureCall;
import corinna.rpc.ReflectionUtil;
import corinna.rpc.RpcValidator;
import corinna.rpc.TypeConverter;
import corinna.soap.core.WsdlGenerator;
import corinna.soap.network.SoapMarshaller;
import corinna.soap.network.SoapUnmarshaller;


// TODO: merge with "javax.bindlet.soap.SoapBindlet"
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
					Object value;
					SOAPElement param = (SOAPElement)current;
					
					if (isElementPOJO(param))
						value = getElementPOJO(param);
					else
						value = getElementContent(param);
					procedure.setParameter(param.getLocalName(), value);
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

	protected boolean isElementPOJO( SOAPElement element )
	{
		
        for (Iterator iter = element.getChildElements(); iter.hasNext();) 
        {
            Object child = iter.next();
            if (child instanceof SOAPElement) return true;
        }
        
        return false;
	}
	
	@SuppressWarnings("rawtypes")
	protected BeanObject getElementPOJO( SOAPElement element )
	{
		BeanObject object = new BeanObject();
		
		// find all procedure parameters
		Iterator it = element.getChildElements();
		while (it.hasNext())
		{
			Object current = it.next();
			if (current instanceof SOAPElement)
			{
				SOAPElement field = (SOAPElement) current;
				String fieldName = field.getElementName().getLocalName();
				Object value;
				if (isElementPOJO(field))
					value = getElementPOJO(field);
				else
					value = getElementContent(field);
				object.set(fieldName, value);
			}
		}
		return object;
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
		SoapUtils.generateElement(element, WsdlGenerator.PARAMETER_RESULT, result);
		
		return message;
	}

	protected SOAPMessage createSoapFault( String namespace, Exception error ) throws SOAPException
	{
		Throwable t = error;
		String text = "";
		SOAPMessage message = SoapUtils.createMessage();
		
		while (t != null && t.getCause() != null)
		{
			t = t.getCause();
		}
		if (t != null) text = t.getMessage(); 
		
		// create the SOAP method response element
		SOAPBody body = message.getSOAPBody();
		QName qname = new QName("fault");
		SOAPElement element = body.addChildElement(qname);
		// create the 'faultcode' element
		qname = new QName("faultcode");
		SOAPElement sub = element.addChildElement(qname);
		sub.setValue("client");
		// create the 'faultstring' element
		qname = new QName("faultstring");
		sub = element.addChildElement(qname);
		sub.setValue(text);
		
		return message;
	}
	
	protected void doPost( IHttpBindletRequest request, IHttpBindletResponse response )
	throws BindletException, IOException
	{
		SOAPMessage resp = null;
		
		try
		{
			// extract the SOAP message from request content
			IProcedureCall call = getProcedureCall(request, response);
			Object result = doCall(call);
			resp = createSoapResponse("", call.getMethodPrototype(), result);
		} catch (Exception e)
		{
			try
			{
				resp = createSoapFault("", e);
			} catch (SOAPException e1)
			{
				response.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
				return;
			}
		}
			
		try
		{
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
	
	@Override
	public Model getBindletModel()
	{
		try
		{
			BindletModel model = (BindletModel) ReflectionUtil.getAnnotation(this.getClass(), BindletModel.class);
			if (model == null) return Model.STATEFULL;
			return model.value();
		} catch (Exception e)
		{
			return Model.STATEFULL;
		}
	}
	
}
