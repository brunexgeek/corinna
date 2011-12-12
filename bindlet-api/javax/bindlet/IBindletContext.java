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


import corinna.util.IComponentInformation;


/**
 * 
 * Defines a set of methods that a servlet uses to communicate with its servlet container, for
 * example, to get the MIME type of a file, dispatch requests, or write to a log file.
 * 
 * <p>
 * There is one context per "web application" per Java Virtual Machine. (A "web application" is a
 * collection of servlets and content installed under a specific subset of the server's URL
 * namespace such as <code>/catalog</code> and possibly installed via a <code>.war</code> file.)
 * 
 * <p>
 * In the case of a web application marked "distributed" in its deployment descriptor, there will be
 * one context instance for each virtual machine. In this situation, the context cannot be used as a
 * location to share global information (because the information won't be truly global). Use an
 * external resource like a database instead.
 * 
 * <p>
 * The <code>IBindletContext</code> object is contained within the {@link IBindletConfig} object,
 * which the service provides the servlet when the servlet is initialized.
 * 
 * @author Bruno Ribeiro
 * @version 1.0
 * @since 1.0
 * 
 * @see IBindlet#getConfiguration()
 * 
 * 
 * @param R
 *            bindlet request type
 * @param P
 *            bindlet response type
 */

public interface IBindletContext extends IObjectSharing
{

	/**
	 * Returns the name and version of the servlet container on which the servlet is running.
	 * 
	 * <p>
	 * The form of the returned string is <i>servername</i>/<i>versionnumber</i>. For example, the
	 * JavaServer Web Development Kit may return the string <code>JavaServer Web Dev Kit/1.0</code>.
	 * 
	 * <p>
	 * The servlet container may return other optional information after the primary string in
	 * parentheses, for example,
	 * <code>JavaServer Web Dev Kit/1.0 (JDK 1.1.6; Windows NT 4.0 x86)</code>.
	 * 
	 * 
	 * @return a <code>IComponentInformation</code> containing he servlet information.
	 * 
	 */
	public IComponentInformation getContextInfo();

	/**
	 * Returns a <code>String</code> containing the value of the named context initialization
	 * parameter, or <code>null</code> if the parameter does not exist.
	 * 
	 * <p>
	 * This method can make available configuration information useful to an entire service.
	 * 
	 * @param name
	 *            a <code>String</code> containing the name of the parameter whose value is
	 *            requested or <code>null</code>
	 * 
	 * @return a <code>String</code> containing the requested value
	 * 
	 * @see IBindletContextConfig#getContextParameter
	 */
	public String getContextParameter( String name );

	/**
	 * Returns the names of the context's initialization parameters as an array of
	 * <code>String</code> objects, or an empty array if the context has no initialization
	 * parameters.
	 * 
	 * @return an array of <code>String</code> objects containing the names of the context's
	 *         initialization parameters
	 * 
	 * @see IBindletContextConfig#getContextParameter
	 */
	public String[] getContextParameterNames();

	/**
	 * Returns the name of this bindlet context as specified in the service deployment descriptor.
	 * 
	 * 
	 * @return a <code>String</code> containing the context name
	 */
	public String getContextName();
	
	public Class<?> getContextRequestType();
	
	public Class<?> getContextResponseType();
	
	public ILogger getLogger();
	
	public void init();
	
	public void destroy();
	
	//public void addListener( IBindletContextListener<?,?> listener );
	
	//public void removeListener( IBindletContextListener<?,?> listener );
	
}
