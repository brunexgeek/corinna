package corinna.soap.bindlet;

import java.io.IOException;

import javax.bindlet.IBindletConfig;
import javax.bindlet.IComponentInformation;
import javax.bindlet.exception.BindletException;
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
import corinna.soap.core.WsdlGenerator;
import corinna.thread.ObjectLocker;
import corinna.util.ComponentInformation;


public class DefaultSoapBindlet extends SoapBindlet
{
	
	private static Logger log = LoggerFactory.getLogger("CorinnaLog");
	
	private static final long serialVersionUID = -5420790590792120345L;
	
	protected static final String DEFAULT_ENDPOINT = "http://localhost:8080";
	
	protected static final String DEFAULT_NAMESPACE = DEFAULT_ENDPOINT + "/" + WsdlGenerator.DEFAULT_SCHEMA;
	
	private static final String PARAMETER_INTERFACE = "interfaceClass";
	
	private static final String PARAMETER_IMPLEMENTATION = "implementationClass";
	
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
	
	public DefaultSoapBindlet( ) throws BindletException
	{
	}

	/**
	 * Change the initialization state to specified value and return <code>true</code> if no actions
	 * must be done (the previous value are the same) or <code>false</code> otherwise.
	 * 
	 * @param value
	 * @return
	 */
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
	}

	@Override
	protected void doGet( IHttpBindletRequest req, IHttpBindletResponse response ) throws BindletException, IOException
	{
		/*String resource = req.getResourcePath();
		if (!resource.equals("/"))
		{
			response.sendError(HttpStatus.NOT_FOUND);
			return;
		}*/

		Definition wsdl = getWsdl(req);
		response.setContentType("text/xml");
		//response.setContentLength(wsdl.length);
		
		BindletOutputStream output = response.getOutputStream();
		//output.write(wsdl);
		WSDLWriter wr;
		try
		{
			wr = WSDLFactory.newInstance().newWSDLWriter();
			wr.writeWSDL(wsdl, output);	
		} catch (WSDLException e)
		{
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
			throw new BindletException( e.getMessage(), e.getCause() );
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
	


	protected Definition generateWsdl( IHttpBindletRequest req ) throws BindletException
	{
		wsdlLock.writeLock();
		try
		{
			ClassDescriptor desc = new ClassDescriptor(runner.getInterfaceClass());
			WsdlGenerator wsdlgen = new WsdlGenerator();
			wsdlName = wsdlgen.getServiceName();
			return (wsdl = wsdlgen.generateWsdl(desc, req.getRequestURL(), null, null));
		} catch (Exception e)
		{
			throw new BindletException("Error generating WSDL", e);
		} finally
		{
			wsdlLock.writeUnlock();
		}
	}

	protected Definition getWsdl( IHttpBindletRequest req ) throws BindletException
	{
		Definition def = null;
		
		wsdlLock.readLock();
		def = wsdl;
		wsdlLock.readUnlock();
		
		if (def != null)
			return def;
		else
			return generateWsdl(req);
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
	
}
