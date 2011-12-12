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

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import corinna.exception.BindletException;
import corinna.exception.ConnectorInUseException;
import corinna.network.INetworkConnector;
import corinna.network.IProtocol;
import corinna.network.NetworkConnector;
import corinna.network.RequestEvent;
import corinna.thread.ObjectLocker;


public final class Domain implements IDomain
{

	private static final Logger log = Logger.getLogger(Domain.class);
	
	private String name;
	
	private ObjectLocker connectorsLock;

	private ObjectLocker serversLock;
	
	private Map<String, INetworkConnector<?, ?>> connectorsByName;
	
	private Map<String, List<INetworkConnector<?, ?>>> connectorsByProtocol;
	
	private Map<String, List<INetworkConnector<?, ?>>> connectorsByServer;

	private Map<String, List<IServer>> serversByConnector;
	
	private Map<String, IServer> servers;
	
	public Domain( String name )
	{
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("The domain name can not be null or empty");
		this.name = name;
		
		connectorsByName = new HashMap<String, INetworkConnector<?, ?>>();
		connectorsByProtocol = new HashMap<String, List<INetworkConnector<?, ?>>>();
		connectorsByServer = new HashMap<String, List<INetworkConnector<?, ?>>>();
		serversByConnector = new HashMap<String, List<IServer>>();
		
		servers = new HashMap<String, IServer>();
		
		connectorsLock = new ObjectLocker();
		serversLock = new ObjectLocker();
	}
	
	@Override
	public INetworkConnector<?, ?> getConnector( String name )
	{
		if (name == null || name.isEmpty()) return null;

		connectorsLock.readLock();
		try
		{
			return connectorsByName.get(name);
		} finally
		{
			connectorsLock.readUnlock();
		}
	}

	/**
	 * Retorna um dos conectores que atendem ao protocolo indicado. Se nenhum conector existir ou
	 * se o índice especificado não existe, é retornado null.
	 */
	@Override
	public INetworkConnector<?, ?> getConnector( IProtocol<?, ?> protocol, int index )
	{
		if (name == null || name.isEmpty()) return null;

		connectorsLock.readLock();
		try
		{
			List<INetworkConnector<?, ?>> list = connectorsByProtocol.get(protocol.toString());
			if (list == null || list.size() <= index) return null;
			return list.get(index);
		} finally
		{
			connectorsLock.readUnlock();
		}
	}

	@Override
	public void addConnector( INetworkConnector<?, ?> connector ) throws ConnectorInUseException
	{
		if (connector == null) return;
		
		connectorsLock.writeLock();
		try
		{
			// set the domain
			if ( !connector.setDomain(this) )
				throw new ConnectorInUseException("The connector can not be added because are in use by another domain");
			// add the connector by name
			connectorsByName.put(connector.getName(), connector);
			// add connector by protocol
			String protocol = connector.getProtocol().toString();
			List<INetworkConnector<?, ?>> list = connectorsByProtocol.get(protocol);
			if (list == null) 
			{
				list = new LinkedList<INetworkConnector<?, ?>>();
				connectorsByProtocol.put(protocol, list);
			}
			list.add(connector);
		} catch (Exception e)
		{
			try
			{
				removeConnector( connector.getName() );
			} catch (ConnectorInUseException er)
			{
				// supress errors
			}
		}
		finally
		{
			connectorsLock.writeUnlock();
		}
	}

	@Override
	public void removeConnector( INetworkConnector<?, ?> connector ) throws ConnectorInUseException
	{
		if (connector == null) return;

		if (connector.getDomain() != this) return;
		
		connectorsLock.writeLock();
		try
		{
			// check if have some server using the connector
			List<IServer> serverList = serversByConnector.get( connector.getName() );
			if (serverList != null && !serverList.isEmpty()) 
				throw new ConnectorInUseException("The connector can not be removed because are in use");
			serversByConnector.remove( connector.getName() );
			// remove connector by name
			connectorsByName.remove( connector.getName() );
			// remove connector by protocol
			List<INetworkConnector<?, ?>> connectorList = connectorsByProtocol.get( connector.getProtocol().toString() );
			if (connectorList != null) connectorList.remove(connector);
			// unset the domain
			connector.setDomain(null);
		} finally
		{
			connectorsLock.writeUnlock();
		}
	}

	@Override
	public void removeConnector( String name ) throws ConnectorInUseException
	{
		if (name == null) return;
		removeConnector( getConnector(name) );
	}

	@Override
	public void removeAllConnectors( IProtocol<?, ?> protocol )
	{

	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public IServer getServer( String name )
	{
		if (name == null) return null;

		serversLock.readLock();
		try
		{
			return servers.get(name);
		} finally
		{
			serversLock.readUnlock();
		}
	}

	@Override
	public void addServer( IServer server, String connectorName )
	{
		if (server == null) return;

		serversLock.writeLock();
		try
		{
			// check if have some connector with that name
			INetworkConnector<?, ?> connector = getConnector(connectorName);
			if (connector == null)
				throw new IllegalArgumentException("Unknown network connector");
			// add server by name
			servers.put(server.getName(), server);
			// add server by connector
			List<IServer> list = serversByConnector.get(connectorName);
			if (list == null)
			{
				list = new LinkedList<IServer>();
				serversByConnector.put(connectorName, list);
			}
			list.add(server);
			// add connector by server
			List<INetworkConnector<?,?>> connectorList = connectorsByServer.get( server.getName() );
			if (connectorList == null)
			{
				connectorList = new LinkedList<INetworkConnector<?,?>>();
				connectorsByServer.put(connectorName, connectorList);
			}
			connectorList.add(connector);
		} finally
		{
			serversLock.writeUnlock();
		}
	}

	@Override
	public void removeServer( IServer server )
	{
		if (server == null) return;

		removeServer( server.getName() );
	}

	@Override
	public void removeServer( String name )
	{
		if (name == null) return;

		serversLock.writeLock();
		try
		{
			// remove the server by name
			IServer server = servers.remove(name);
			if (server == null) return;
			// remove the server by connector
			List<INetworkConnector<?, ?>> connectorList = connectorsByServer.get(name);
			if (connectorList == null || connectorList.isEmpty()) return;
			for ( INetworkConnector<?, ?> connector : connectorList )
			{
				List<IServer> serverList = serversByConnector.get( connector.getName() );
				if (serverList == null || serverList.isEmpty()) continue;
				serverList.remove(server);
			}
			connectorList.clear();
			connectorsByServer.remove(name);
		} finally
		{
			serversLock.writeUnlock();
		}
	}
	
	@Override
	public void connectorRequestReceived( NetworkConnector<?, ?> connector,
		RequestEvent<?, ?> event ) throws BindletException, IOException
	{
		serversLock.readLock();
		try
		{
			// obtém a lista de servidores que utilizam o conector
			List<IServer> list = serversByConnector.get( connector.getName() );
			if (list == null || list.isEmpty()) return;
			// itera entre os servidores
			for (IServer server : list)
			{
				if (log.isDebugEnabled())
					log.debug("Dispatching event to server " + server.getName());
				server.domainRequestReceived(this, event);
				if ( event.isHandled() ) break;
			}
		} finally
		{
			serversLock.readUnlock();
		}
	}
	
}
