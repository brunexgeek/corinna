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
import corinna.network.INetworkConnector;
import corinna.network.INetworkConnectorListener;
import corinna.network.IProtocol;


public interface IDomain extends INetworkConnectorListener
{

	public String getName();
	
	public INetworkConnector<?, ?> getConnector( String name );
	
	public INetworkConnector<?,?> getConnector( IProtocol<?,?> protocol, int index );
	
	public void addConnector( INetworkConnector<?,?> connector ) throws ConnectorInUseException;
	
	public void removeConnector( INetworkConnector<?,?> connector ) throws ConnectorInUseException;
	
	public void removeConnector( String name ) throws ConnectorInUseException;
		
	public void removeAllConnectors( IProtocol<?,?> protocol );
	
	public IServer getServer( String name );
	
	public void addServer( IServer server );
	
	public IServer removeServer( IServer server );
	
	public IServer removeServer( String name );
	
}
