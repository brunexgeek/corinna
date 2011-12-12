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


/**
 * Define the methods for an object used by a bindlet container to pass information to a bindlet
 * during it's initialization.
 * 
 * @author Bruno Ribeiro
 * @version 1.0
 * @since 1.0
 */
public interface IBindletConfig
{

	/**
	 * Returns the name of this bindlet instance. The name may be provided via domain
	 * administration, assigned in the service deployment descriptor, or be the bindlet's class
	 * name.
	 * 
	 * @return the name of the servlet instance
	 */
	public String getBindletName();
	
	/**
	 * Returns a reference to the {@link IBindletContext} in which the bindlet will be associated.
	 * 
	 * @return a {@link IBindletContext} object in which the bindlet will be associated
	 * 
	 * @see IBindletContext
	 */
	public IBindletContext getBindletContext();
	
	/**
	 * Returns a <code>String</code> containing the value of the named initialization parameter, or
	 * <code>null</code> if the parameter does not exist.
	 * 
	 * @param name
	 *            a <code>String</code> specifying the name of the initialization parameter
	 * 
	 * @return a <code>String</code> containing the value of the initialization parameter
	 */
	public String getBindletParameter( String name );
	
	/**
	 * Returns the names of the bindlet's initialization parameters as an array of
	 * <code>String</code> objects, or an empty array if the bindlet has no initialization
	 * parameters.
	 * 
	 * @return an array of <code>String</code> objects containing the names of the bindlet's
	 *         initialization parameters
	 */
	public String[] getBindletParameterNames();

}
