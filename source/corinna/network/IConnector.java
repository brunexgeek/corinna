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
package corinna.network;


import java.net.SocketAddress;

import corinna.core.ILifecycle;
import corinna.core.IServer;


/**
 * Defines a network connector interface. A network connector is responsible to handle incoming
 * connections from clients through a specific protocol.
 * 
 * @author Bruno Ribeiro
 * @since 1.0
 * @version 1.0
 */
public interface IConnector extends ILifecycle
{

	public Class<?>[] CONSTRUCTOR_ARGS = { IConnectorConfig.class };

	public IServer getServer();

	public boolean setServer( IServer domain );

	public IProtocol getProtocol();

	public String getName();

	public SocketAddress getAddress();

	public String getParameter( String name );

	public void setParameter( String name, String value );

	public String[] getParameterNames();
	
}
