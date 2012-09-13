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

import corinna.exception.ConnectorInUseException;
import corinna.network.IConnector;
import corinna.network.IConnectorListener;
import corinna.network.IProtocol;



/**
 * Define an interface for server implementations.
 * 
 * @author Bruno Ribeiro
 * @since 2.0
 * @version 2.0
 */
public interface IServer extends ILifecycle, IConnectorListener, IServiceEventListener
{

	public static final Class<?>[] CONSTRUCTOR_ARGS = { IServerConfig.class };

	public String getName();

	public String[] getServiceNames();
	
	public IService getService( String name );
	
	public void addService( IService service );
		
	public IService removeService( IService service );
	
	public IService removeService( String name );
	
	public IConnector getConnector( String name );
	
	public IConnector getConnector( IProtocol<?,?> protocol, int index );
	
	public void addConnector( IConnector connector ) throws ConnectorInUseException;
	
	public void removeConnector( IConnector connector ) throws ConnectorInUseException;
	
	public void removeConnector( String name ) throws ConnectorInUseException;
		
	public void removeAllConnectors( IProtocol<?,?> protocol );
	
	public IDomain getDomain();
	
	public boolean setDomain( IDomain domain );

	public IServerConfig getConfig();
	
}
