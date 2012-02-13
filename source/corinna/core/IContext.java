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

package corinna.core;


import javax.bindlet.IBindlet;
import javax.bindlet.IBindletContext;

import corinna.exception.BindletException;
import corinna.exception.ConfigurationNotFoundException;
import corinna.util.IComponentInformation;
import corinna.util.conf.ISection;

public interface IContext<R,P> extends IServiceRequestListener, ILifecycle
{

	public Class<?>[] CONSTRUCTOR_ARGS = { IContextConfig.class, IService.class };

	public IService getService();
	
	public IBindletContext getBindletContext();
	
	public String getName();
	
	public IBindletRegistration addBindlet( String bindletName, String bindletClassName )
		throws BindletException, ClassNotFoundException;

	public IBindletRegistration addBindlet( String bindletName,
		Class<? extends IBindlet<R, P>> bindletClass ) throws BindletException;

	public IBindlet<R, P> createBindlet( String bindletName ) throws BindletException;

	public boolean containsBindlet( String bindletName );

	public Class<?> getRequestType();
	
	public Class<?> getResponseType();
	
	public IBindletRegistration getBindletRegistration( String name );

	public IBindletRegistration[] getBindletRegistrations( );
	
	public IBindletRegistration getBindletRegistration( R request );
	
	public IComponentInformation getContextInfo();

	public IContextConfig getConfig();
	
}
