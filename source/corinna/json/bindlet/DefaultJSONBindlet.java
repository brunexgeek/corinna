package corinna.json.bindlet;

import javax.bindlet.BindletModel;
import javax.bindlet.IBindletConfig;
import javax.bindlet.BindletModel.Model;
import javax.bindlet.exception.BindletException;
import javax.bindlet.rpc.IProcedureCall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import corinna.rpc.CanonicalPrototypeFilter;
import corinna.rpc.IPrototypeFilter;
import corinna.rpc.MethodRunner;
import corinna.rpc.ReflectionUtil;
import corinna.util.StateModel;


@BindletModel(Model.STATELESS)
public class DefaultJSONBindlet extends JSONBindlet
{

	private static final long serialVersionUID = -7776689284640470657L;

	private static final Logger log = LoggerFactory.getLogger(DefaultJSONBindlet.class);

	public static final String PARAMETER_INTERFACE = "interfaceClass";
	
	public static final String PARAMETER_IMPLEMENTATION = "implementationClass";
	
	private MethodRunner runner = null;
	
	public DefaultJSONBindlet() throws BindletException
	{
		super();
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
		
		// check if the component and the bindlet have a compatible model
		StateModel classModel = (StateModel) ReflectionUtil.getAnnotation(intfClass, StateModel.class);
		if (classModel == null)
			classModel = (StateModel) ReflectionUtil.getAnnotation(implClass, StateModel.class);
		BindletModel bindModel = (BindletModel) ReflectionUtil.getAnnotation(this.getClass(), BindletModel.class);
		if (bindModel != null)
		{
			if (bindModel.value() == BindletModel.Model.STATELESS && 
			    (classModel == null || classModel.value() != BindletModel.Model.STATELESS))
				throw new BindletException("Both the bindlet and the component should be stateless");
		}
		
		try
		{
			IPrototypeFilter filter = new CanonicalPrototypeFilter();
			runner = new MethodRunner(intfClass, implClass, filter, null);
		} catch (Exception e)
		{
			throw new BindletException("Error creating the method runner", e);
		}
	}

	@Override
	public Object doCall( IProcedureCall procedure ) throws BindletException
	{
		if (log.isTraceEnabled())
			log.trace("Received procedure call: " + procedure);

		try
		{
			return runner.callMethod(procedure);
		} catch (Exception e)
		{
			throw new BindletException("Error invoking method", e);
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
	
}
