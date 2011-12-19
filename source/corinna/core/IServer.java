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

import java.util.List;



/**
 * Define an interface for server implementations.
 * 
 * @author Bruno Ribeiro
 * @since 2.0
 * @version 2.0
 */
public interface IServer extends ILifecycle, IServiceEventListener, IDomainListener
{

	public static final Class<?>[] CONSTRUCTOR_ARGS = { String.class };

	public String getName();
	
	/**
	 * Returns a list containing all registered services on the server.
	 * 
	 * @return {@link List}&lt;{@link IService}&gt; instance containing the registred services.
	 */
	public List<IService> getServices();
	
	public IService getService( String name );
	
	public void addService( IService service );
		
	public IService removeService( IService service );
	
	public IService removeService( String name );
	
	public IDomain getDomain();
	
	public boolean setDomain( IDomain domain );
	
}
