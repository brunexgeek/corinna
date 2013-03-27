/*
 * Copyright 2011-2013 Bruno Ribeiro
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

package corinna.core;


import javax.bindlet.BindletModel;
import javax.bindlet.IBindlet;
import javax.bindlet.IBindletConfig;
import javax.bindlet.exception.BindletException;


/**
 * <p>
 * Basic interface for a bindlet registration object. A bindlet registration is responsible to store
 * all necessary informations to create bindlet instances. The interface define the
 * {@link #createBindlet()} method whereby a container object (probably an object that extends
 * {@link Context} class) can create or reuse bindlet instances to process incoming requests.
 * </p>
 * 
 * <p>
 * All operations in this class must be thread-safe.
 * </p>
 * 
 * @author Bruno Ribeiro
 */
public interface IBindletRegistration
{

	/**
	 * Return the class that implements this bindlet.
	 * 
	 * @return
	 */
	public Class<? extends IBindlet<?, ?>> getBindletClass();

	/**
	 * Return the fully qualified bindlet class name for this bindlet.
	 */
	public String getBindletClassName();

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

	public void setBindletParameter( String name, String value );

	/**
	 * Is this servlet currently unavailable?
	 */
	public boolean isUnavailable();

	public void setUnavailable( boolean value );

	/**
	 * <p>
	 * Returns a bindlet instance according to the following conditions:
	 * </p>
	 * 
	 * <ul>
	 * <li>If the bindlet is stateless, all calls for this method will return the same bindlet
	 * instance;</li>
	 * <li>If the bindlet is recyclable, one or more calls for this method will return the same
	 * bindlet instance, but is garanteed that the returned instance can be used as if it were
	 * statefull;</li>
	 * <li>If the bindlet is statefull, every call for this method will return a new instance of the
	 * bindlet.</li>
	 * </ul>
	 * <p>
	 * Additionally, stateless bindlets will be initialized through a call for <code>init</code>
	 * method when the singleton instance were created.
	 * </p>
	 */
	public IBindlet<?, ?> createBindlet() throws BindletException;

	public boolean isLoadOnStartup();

	public void setLoadOnStartup( boolean value );

	public IBindletConfig getBindletConfig();

	public BindletModel.Model getBindletModel();

	public String getBindletName();

	void releaseBindlet( IBindlet<?, ?> bindlet ) throws BindletException;

}
