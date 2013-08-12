/*
 * Copyright 2011-2012 Bruno Ribeiro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package corinna.rest.bindlet;


import javax.bindlet.BindletModel;
import javax.bindlet.BindletModel.Model;
import javax.bindlet.IBindletConfig;
import javax.bindlet.exception.BindletException;
import javax.bindlet.rpc.IProcedureCall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import corinna.rpc.CanonicalPrototypeFilter;
import corinna.rpc.IPrototypeFilter;
import corinna.rpc.MethodRunner;
import corinna.rpc.ReflectionUtil;
import corinna.util.StateModel;


/**
 * A REST bindlet that translates HTTP GET/POST messages into RPC calls.
 * 
 * <h1>Custom parameters</h1>
 * 
 * <p>
 * This bindlet support a set of parameters through which it's possible to customize the bindlet
 * behavior. The following list show all supported parameters for this implementation:
 * </p>
 * 
 * <ul>
 * <li><strong>rpc.interfaceClass:</strong> qualified class name of the class that defines the web
 * service interface.</li>
 * <li><strong>rpc.implementationClass:</strong> qualified class name of the class that implements
 * the web service interface.</li>
 * <li><strong>rpc.compatibilityMode:</strong> defines whether the bindlet must accept the old way
 * to specify the method name (through REST parameter 'method').</li>
 * </ul>
 * 
 * @version 1.1
 * @since 1.0
 * @author Bruno Ribeiro
 */
@BindletModel(Model.STATELESS)
public class DefaultRestBindlet extends RestBindlet
{

	private static final long serialVersionUID = -579681351761760082L;

	private static final Logger log = LoggerFactory.getLogger(DefaultRestBindlet.class);

	/**
	 * Parameter name to define the qualified class name of the class that defines the web service
	 * interface
	 */
	public static final String PARAMETER_INTERFACE = "rpc.interfaceClass";

	/**
	 * Parameter name to define the qualified class name of the class that implements the web
	 * service.
	 */
	public static final String PARAMETER_IMPLEMENTATION = "rpc.implementationClass";

	/**
	 * Parameter name to define whether the bindlet must accept the old way to specify the method
	 * name (through REST parameter 'method').
	 */
	//public static final String PARAMETER_COMPATILITY = "rpc.compatibilityMode";
	
	/**
	 * Instance of the method runner used to access the web service methods.
	 */
	private MethodRunner runner = null;

	public DefaultRestBindlet() throws BindletException
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
			throw new BindletException("The interface class must be specified through "
				+ "bindlet configuration key '" + PARAMETER_INTERFACE + "'");
		// load the implementation class name
		String implClassName = config.getBindletParameter(PARAMETER_IMPLEMENTATION);
		if (implClassName == null || implClassName.isEmpty())
			throw new BindletException("The implementation class must be specified through "
				+ "bindlet configuration key '" + PARAMETER_IMPLEMENTATION + "'");

		Class<?> intfClass = loadClass(intfClassName);
		Class<?> implClass = loadClass(implClassName);

		// check if the component and the bindlet have a compatible model
		StateModel classModel = (StateModel) ReflectionUtil.getAnnotation(intfClass,
			StateModel.class);
		if (classModel == null)
			classModel = (StateModel) ReflectionUtil.getAnnotation(implClass, StateModel.class);
		BindletModel bindModel = (BindletModel) ReflectionUtil.getAnnotation(this.getClass(),
			BindletModel.class);
		if (bindModel != null)
		{
			if (bindModel.value() == BindletModel.Model.STATELESS
				&& (classModel == null || classModel.value() != BindletModel.Model.STATELESS))
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
		if (log.isTraceEnabled()) log.trace("Received procedure call: " + procedure);

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
