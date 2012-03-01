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
import corinna.exception.ServerInUseException;
import corinna.network.IConnector;
import corinna.network.IConnectorListener;
import corinna.network.IProtocol;


public interface IDomain extends IConnectorListener, ILifecycle
{

	public String getName();
	
	public IConnector getConnector( String name );
	
	public IConnector getConnector( IProtocol<?,?> protocol, int index );
	
	public void addConnector( IConnector connector ) throws ConnectorInUseException;
	
	public void removeConnector( IConnector connector ) throws ConnectorInUseException;
	
	public void removeConnector( String name ) throws ConnectorInUseException;
		
	public void removeAllConnectors( IProtocol<?,?> protocol );
	
	public IServer getServer( String name );
	
	public void addServer( IServer server ) throws ServerInUseException;
	
	public IServer removeServer( IServer server ) throws ServerInUseException;
	
	public IServer removeServer( String name ) throws ServerInUseException;
	
}
