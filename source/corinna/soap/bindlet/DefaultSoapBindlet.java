package corinna.soap.bindlet;

import java.io.IOException;
import java.util.Iterator;

import javax.bindlet.BindletOutputStream;
import javax.bindlet.IBindletConfig;
import javax.bindlet.http.HttpStatus;
import javax.bindlet.soap.ISoapBindletRequest;
import javax.bindlet.soap.ISoapBindletResponse;
import javax.bindlet.soap.SoapBindlet;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.Text;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.HttpHeaders;

import corinna.auth.bindlet.IBindletAuthenticator;
import corinna.exception.BindletException;
import corinna.http.core.auth.AuthenticateResponse;
import corinna.http.core.auth.DigestAuthenticator;
import corinna.http.core.auth.IHttpAuthenticator;
import corinna.service.rpc.ClassDescriptor;
import corinna.service.rpc.IPrototypeFilter;
import corinna.service.rpc.MethodRunner;
import corinna.service.rpc.ProcedureCall;
import corinna.thread.ObjectLocker;


public class DefaultSoapBindlet extends SoapBindlet
{
	
	private static Logger log = Logger.getLogger(DefaultSoapBindlet.class);
	
	private static final long serialVersionUID = -5420790590792120345L;
	
	protected static final String DEFAULT_ENDPOINT = "http://localhost:8080";
	
	protected static final String DEFAULT_NAMESPACE = DEFAULT_ENDPOINT + "/" + WsdlGenerator.DEFAULT_SCHEMA;
	
	private static final String PARAMETER_INTERFACE = "interfaceClass";
	
	private static final String PARAMETER_IMPLEMENTATION = "implementationClass";

	private static final String PARAM_REQUEST = "_request";
	
	private static final String PARAM_RESPONSE = "_response";
	
	private MethodRunner runner;
	
	private String wsdl = null;
	
	private ObjectLocker wsdlLock = new ObjectLocker();
	
	protected IBindletAuthenticator authenticator = null;
	
	public DefaultSoapBindlet( ) throws BindletException
	{
	}

	@Override
	public void init() throws BindletException
	{
		IBindletConfig config = getBindletConfig();

		// load the interface class name
		String intfClassName = config.getBindletParameter(PARAMETER_INTERFACE);
		if (intfClassName == null || intfClassName.isEmpty())
			throw new BindletException("The interface class must be specified through " +
				"bindlet configuration key '" + PARAMETER_INTERFACE + "'");
		// load the implementation class name
		String implClassName = config.getBindletParameter(PARAMETER_IMPLEMENTATION);
		if (implClassName == null || implClassName.isEmpty())
			throw new BindletException("The implementation class must be specified through " +
				"bindlet configuration key '" + PARAMETER_IMPLEMENTATION + "'");
		
		Class<?> intfClass = loadClass(intfClassName);
		Class<?> implClass = loadClass(implClassName);
		
		try
		{
			IPrototypeFilter filter = new SoapPrototypeFilter();
			runner = new MethodRunner(intfClass, implClass, filter, null);
		} catch (Exception e)
		{
			throw new BindletException("Error creating the method runner", e);
		}
	}

	@Override
	protected void doWsdl( ISoapBindletRequest req, ISoapBindletResponse response ) throws BindletException, IOException
	{
		String resource = req.getResourcePath();
		if (!resource.equals("/"))
		{
			response.sendError(HttpStatus.NOT_FOUND);
			return;
		}
		
		String wsdl = getWsdl(req);
		response.setContentType("text/xml");
		response.setContentLength(wsdl.length());
		
		BindletOutputStream output = response.getOutputStream();
		output.write(wsdl);
		output.close();
	}

	@Override
	protected void doPost( ISoapBindletRequest req, ISoapBindletResponse res ) throws BindletException, 
		IOException
	{
		ProcedureCall procedure = parseSoapMessage( req.getMessage() );
		procedure.setParameter(PARAM_REQUEST, req);
		procedure.setParameter(PARAM_RESPONSE, res);

		if (log.isTraceEnabled())
			log.trace("Received method call for '" + procedure + "'");
		
		try
		{
			// call the method and generate the SOAP response message
			Object result = runner.callMethod(procedure);
			SOAPMessage response = createSoapResponse(DEFAULT_NAMESPACE, procedure.getMethodPrototype(), result);
			
			res.setChunked(false);
			res.setContentType("text/xml");
			res.setMessage(response);
		} catch (Exception e)
		{
			log.error(e);
			throw new IOException("Error generating output SOAP/XML", e);
		}
	}

	@SuppressWarnings("rawtypes")
	protected ProcedureCall parseSoapMessage( SOAPMessage message )
	{
		SOAPElement soapMethod = null;
		
		if (message == null) return null;
		
		try
		{
			SOAPBody body = message.getSOAPBody();
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

			String soapMethodName = soapMethod.getElementQName().getLocalPart();
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
			return procedure;
		} catch (Exception e)
		{
			log.error(e);
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
	
	protected Class<?> loadClass( String name ) throws BindletException
	{
		try
		{
			return Class.forName(name);
		} catch (ClassNotFoundException e)
		{
			throw new BindletException("Error loading class '" + name + "'", e);
		}
	}
	
	protected SOAPMessage createSoapResponse( String namespace, String prototype, Object result ) throws SOAPException
	{
		SOAPMessage message = SoapUtils.createMessage();
		
		// create the SOAP method response element
		SOAPBody body = message.getSOAPBody();
		QName qname = new QName(namespace, prototype + WsdlGenerator.METHOD_RESPONSE_SUFFIX, "");
		SOAPElement element = body.addChildElement(qname);
		// create the return value element
		qname = new QName(namespace, WsdlGenerator.PARAMETER_RESULT, "");
		SOAPElement carrier = element.addChildElement(qname);
		carrier.setValue( result.toString() );
		
		return message;
	}

	protected String generateWsdl( ISoapBindletRequest req ) throws BindletException
	{
		wsdlLock.writeLock();
		try
		{
			ClassDescriptor desc = new ClassDescriptor(runner.getInterfaceClass());
			WsdlGenerator wsdlgen = new WsdlGenerator(req.getRequestURL());
			return (wsdl = wsdlgen.generateWsdl(desc));
		} catch (Exception e)
		{
			throw new BindletException("Error generating WSDL", e);
		} finally
		{
			wsdlLock.writeUnlock();
		}
	}

	protected String getWsdl( ISoapBindletRequest req ) throws BindletException
	{
		boolean hasWsdl = true;
		String text = null;
		
		wsdlLock.readLock();
		hasWsdl = (wsdl != null);
		text = wsdl;
		wsdlLock.readUnlock();
		
		if (hasWsdl)
			return text;
		else
			return generateWsdl(req);
	}
	
	@Override
	protected boolean doAuthentication( ISoapBindletRequest request, ISoapBindletResponse response )
		throws BindletException, IOException
	{
		if (authenticator != null)
			return authenticator.authenticate(request, response);
		else
			return false;
	}

	@Override
	public boolean isRestricted()
	{
		return (authenticator != null);
	}
	
}
