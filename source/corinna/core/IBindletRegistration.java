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

package corinna.core;

import javax.bindlet.BindletModel;
import javax.bindlet.IBindlet;
import javax.bindlet.IBindletConfig;
import javax.bindlet.exception.BindletException;



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
	 * Returns a bindlet instance according to the following conditions:
	 * 
	 * <ul>
	 * <li>If the bindlet is stateless, all calls for this method will be return the same bindlet
	 * instance;</li>
	 * <li>If the bindlet is recyclable, one or more calls for this method will be return the same
	 * bindlet instance, but is garanteed that the returned instance can be used as if it were
	 * statefull;</li>
	 * <li>If the bindlet is statefull, every call for this method will be return a new instance of
	 * the bindlet.</li>
	 * </ul>
	 */
	public IBindlet<?, ?> createBindlet() throws BindletException;

	public boolean isLoadOnStartup();
	
	public void setLoadOnStartup( boolean value );

	public IBindletConfig getBindletConfig();
	
	public BindletModel.Model getBindletModel();
	
	public String getBindletName();

	void releaseBindlet( IBindlet<?, ?> bindlet ) throws BindletException;

}
