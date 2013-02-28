package corinna.soap.bindlet;

import java.io.IOException;

import javax.bindlet.IBindletConfig;
import javax.bindlet.IComponentInformation;
import javax.bindlet.exception.BindletException;
import javax.bindlet.http.HttpStatus;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;
import javax.bindlet.io.BindletOutputStream;
import javax.bindlet.rpc.IProcedureCall;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import corinna.rpc.CanonicalPrototypeFilter;
import corinna.rpc.ClassDescriptor;
import corinna.rpc.IPrototypeFilter;
import corinna.rpc.MethodRunner;
import corinna.soap.core.WSDLGenerator;
import corinna.thread.ObjectLocker;
import corinna.util.ComponentInformation;


public class DefaultSOAPBindlet extends SOAPBindlet
{
	
	private static Logger log = LoggerFactory.getLogger("CorinnaLog");
	
	private static final long serialVersionUID = -5420790590792120345L;
	
	private static final String PARAMETER_INTERFACE = "interfaceClass";
	
	private static final String PARAMETER_IMPLEMENTATION = "implementationClass";
	
	private static final String PARAMETER_XMLSCHEMA_NAMESPACE = "XMLSchemaNamespace";
	
	private static final String PARAMETER_WSDL_NAMESPACE = "WSDLNamespace";
	
	private static final String PARAMETER_ENDPOINT = "endpointURL";
	
	private static final String COMPONENT_NAME = "SOAP Web Service Bindlet";
	
	private static final String COMPONENT_VERSION = "1.0";
	
	private static final String COMPONENT_IMPLEMENTOR = "Bruno Ribeiro";
	
	private static final IComponentInformation bindletInfo = new ComponentInformation(COMPONENT_NAME,
		COMPONENT_VERSION, COMPONENT_IMPLEMENTOR);
	
	private MethodRunner runner;
	
	private Definition wsdl = null;
	
	private ObjectLocker wsdlLock = new ObjectLocker();
	
	private Boolean isInitialized = false;

	private String wsdlName;

	private String localXMLSchemaNamespace = null;

	private String localWSDLNamespace = null;
	
	private String endpointURL = null;
	
	public DefaultSOAPBindlet( ) throws BindletException
	{
	}

	/**
	 * Change the initialization state to specified value and return <code>true</code> if no actions
	 * must be done (the previous value are the same) or <code>false</code> otherwise.
	 * 
	 * @param value 
	 * @return
	 */
	// TODO: what this means?
	private boolean setInitialized( boolean value )
	{
		synchronized (isInitialized)
		{
			if (value)
			{
				if (isInitialized) return true;
				return !(isInitialized = true);
			}
			else
			{
				if (!isInitialized) return true;
				return (isInitialized = false);
			}
		}
	}
	
	@Override
	public void init() throws BindletException
	{
		// if the bindlet are running in stateless model, ensure will be initialized only once
		if (setInitialized(true)) return;

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
		// load the XML Schema and WSDL namespaces
		localXMLSchemaNamespace = config.getBindletParameter(PARAMETER_XMLSCHEMA_NAMESPACE);
		localWSDLNamespace = config.getBindletParameter(PARAMETER_WSDL_NAMESPACE);
		// load the endpoint URL
		endpointURL = config.getBindletParameter(PARAMETER_ENDPOINT);
		if (implClassName == null || implClassName.isEmpty())
			throw new BindletException("The endpoint URL must be specified through " +
				"bindlet configuration key '" + PARAMETER_ENDPOINT + "'");

		Class<?> intfClass = loadClass(intfClassName);
		Class<?> implClass = loadClass(implClassName);
		
		try
		{
			IPrototypeFilter filter = new CanonicalPrototypeFilter();
			runner = new MethodRunner(intfClass, implClass, filter, null);
		} catch (Exception e)
		{
			log.error("Error creating the method runner ", e);
			throw new BindletException("Error creating the method runner", e);
		}
		
		// force the generation of the WSDL
		generateWsdl();
	}

	@Override
	protected void doGet( IHttpBindletRequest req, IHttpBindletResponse response ) throws BindletException, IOException
	{
		String resource = req.getResourcePath();
		if (!resource.equals("/"))
		{
			response.sendError(HttpStatus.NOT_FOUND);
			return;
		}

		Definition wsdl = getWsdl();
		response.setContentType("text/xml");
		
		BindletOutputStream output = response.getOutputStream();
		WSDLWriter wr;
		try
		{
			wr = WSDLFactory.newInstance().newWSDLWriter();
			wr.writeWSDL(wsdl, output);	
		} catch (WSDLException e)
		{
			log.error("Error generating WSDL", e);
		}
			
		output.close();
	}

	@Override
	protected Object doCall( IProcedureCall procedure ) throws BindletException
	{
		if (log.isTraceEnabled())
			log.trace("Received method call '" + procedure + "'");
		
		try
		{
			// call the method and generate the SOAP response message
			return runner.callMethod(procedure);
		} catch (Exception e)
		{
			throw new BindletException("Error invoking RPC method", e);
		}
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
	


	protected Definition generateWsdl( ) throws BindletException
	{
		wsdlLock.writeLock();
		try
		{
			ClassDescriptor desc = new ClassDescriptor(runner.getInterfaceClass());
			WSDLGenerator wsdlgen = new WSDLGenerator( desc, endpointURL, localWSDLNamespace , localXMLSchemaNamespace );
			localXMLSchemaNamespace = wsdlgen.getXMLSchemaNamespace();
			wsdlName = wsdlgen.getServiceName();
			return (wsdl = wsdlgen.generateWsdl());
		} catch (Exception e)
		{
			log.error("Error generating WSDL", e);
			throw new BindletException("Error generating WSDL", e);
		} finally
		{
			wsdlLock.writeUnlock();
		}
	}

	protected Definition getWsdl( ) throws BindletException
	{
		Definition def = null;
		
		wsdlLock.readLock();
		def = wsdl;
		wsdlLock.readUnlock();
		
		if (def != null)
			return def;
		else
			return generateWsdl();
	}

	protected String getWsdlName()
	{
		wsdlLock.readLock();
		String value = wsdlName;
		wsdlLock.readUnlock();
		
		return value;
	}
	
	@Override
	public IComponentInformation getBindletInfo()
	{
		return bindletInfo;
	}

	@Override
	public String getXMLSchemaNamespace()
	{
		return localXMLSchemaNamespace;
	}
	
}
