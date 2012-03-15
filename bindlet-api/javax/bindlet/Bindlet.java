/*
 * Copyright 2011 Bruno Ribeiro <brunei@users.sourceforge.net>
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

package javax.bindlet;


import java.io.Serializable;

import javax.bindlet.exception.BindletException;



/**
 * 
 * Defines a generic, protocol-independent bindletlet. To write an HTTP bindlet or SOAP bindlet for use on the Web,
 * extend {@link corinna.service.bindlet.http.HttpBindlet} or {@link corinna.service.bindlet.soap.SoapBindlet} instead.
 * 
 * <p>
 * <code>GenericServlet</code> implements the <code>Servlet</code> and <code>ServletConfig</code>
 * interfaces. <code>GenericServlet</code> may be directly extended by a servlet, although it's more
 * common to extend a protocol-specific subclass such as <code>HttpServlet</code>.
 * </p>
 * 
 * <p>
 * <code>GenericServlet</code> makes writing servlets easier. It provides simple versions of the
 * lifecycle methods <code>init</code> and <code>destroy</code> and of the methods in the
 * <code>ServletConfig</code> interface. <code>GenericServlet</code> also implements the
 * <code>log</code> method, declared in the <code>ServletContext</code> interface.
 * </p>
 * 
 * <p>
 * To write a generic servlet, you need only override the abstract <code>service</code> method.
 * </p>
 * 
 * @author Bruno Ribeiro
 * @version Bindlet 1.0
 * @since Bindlet 1.0
 */
@SuppressWarnings("serial")
public abstract class Bindlet<R, P> implements IBindlet<R, P>, Serializable
{

	private IBindletConfig config = null;
		
	/**
	 * Does nothing. All of the bindlet initialization is done by one of the <code>init</code>
	 * methods.
	 */
	public Bindlet( ) throws BindletException
	{
	}

	/**
	 * Returns a <code>String</code> containing the value of the named initialization parameter, or
	 * <code>null</code> if the parameter does not exist. See
	 * {@link IBindletConfig#getInitParameter}.
	 * 
	 * <p>
	 * This method is supplied for convenience. It gets the value of the named parameter from the
	 * servlet's <code>ServletConfig</code> object.
	 * 
	 * @param name
	 *            a <code>String</code> specifying the name of the initialization parameter
	 * 
	 * @return String a <code>String</code> containing the value of the initialization parameter
	 * 
	 */
	public String getInitParameter( String name )
	{
		IBindletConfig config = getBindletConfig();
		if (config == null) return null;
		return config.getBindletParameter(name);
	}

	/**
	 * Returns the names of the servlet's initialization parameters as an <code>Enumeration</code>
	 * of <code>String</code> objects, or an empty <code>Enumeration</code> if the servlet has no
	 * initialization parameters. See {@link IBindletConfig#getInitParameterNames}.
	 * 
	 * <p>
	 * This method is supplied for convenience. It gets the parameter names from the servlet's
	 * <code>ServletConfig</code> object.
	 * 
	 * 
	 * @return Enumeration an enumeration of <code>String</code> objects containing the names of the
	 *         servlet's initialization parameters
	 * 
	 */
	public String[] getInitParameterNames()
	{
		IBindletConfig config = getBindletConfig();
		if (config == null) return new String[0];
		return config.getBindletParameterNames();
	}


	/**
	 * Returns a reference to the {@link IBindletContext} in which this servlet is running. See
	 * {@link IBindletConfig#getServletContext}.
	 * 
	 * <p>
	 * This method is supplied for convenience. It gets the context from the servlet's
	 * <code>ServletConfig</code> object.
	 * 
	 * 
	 * @return ServletContext the <code>ServletContext</code> object passed to this servlet by the
	 *         <code>init</code> method
	 * 
	 */
	public IBindletContext getBindletContext()
	{
		IBindletConfig config = getBindletConfig();
		if (config == null) return null;
		return (IBindletContext) config.getBindletContext();
	}

	/**
	 * Returns information about the servlet, such as author, version, and copyright. By default,
	 * this method returns an empty string. Override this method to have it return a meaningful
	 * value. See {@link IBindlet#getServletInfo}.
	 * 
	 * 
	 * @return String information about this servlet, by default an empty string
	 * 
	 */
	@Override
	public abstract IComponentInformation getBindletInfo();

	@Override
	public final void init( IBindletConfig config ) throws BindletException
	{
		if (config == null)
			throw new IllegalArgumentException("The bindlet configuration can not be null");
		if (config.getBindletContext() == null)
			throw new BindletException("The bindlet context is required");
		this.config = config;

		init();
	}
	
	@Override
	public void init(  ) throws BindletException
	{
		// does nothing
	}
	
	/**
	 * Called by the servlet container to indicate to a servlet that the servlet is being taken out
	 * of service. See {@link IBindlet#destroy}.
	 * 
	 * 
	 */
	@Override
	public void destroy()
	{
		// does nothing
	}
	
	@Override
	public IBindletConfig getBindletConfig()
	{
		return config;
	}

	/**
	 * Returns the name of this servlet instance. See {@link IBindletConfig#getServletName}.
	 * 
	 * @return the name of this servlet instance
	 */
	@Override
	public String getBindletName()
	{
		IBindletConfig config = getBindletConfig();
		if (config == null) return null;
		return config.getBindletName();
	}
	
}
