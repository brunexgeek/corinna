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
import corinna.exception.ServerInUseException;
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
	
	private Map<String, IServer> servers;
	
	public Domain( String name )
	{
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("The domain name can not be null or empty");
		this.name = name;
		
		connectorsByName = new HashMap<String, INetworkConnector<?, ?>>();
		connectorsByProtocol = new HashMap<String, List<INetworkConnector<?, ?>>>();
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
				// supress any errors
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
			// set the domain
			if ( !connector.setDomain(null) )
				throw new ConnectorInUseException("The connector can not be removed because are in use by the domain");
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
	public void addServer( IServer server ) throws ServerInUseException
	{
		if (server == null) return;

		serversLock.writeLock();
		try
		{
			// set the domain
			if ( !server.setDomain(this) )
				throw new ServerInUseException("The connector can not be removed because are in use by another domain");
			// add server by name
			servers.put(server.getName(), server);
		} finally
		{
			serversLock.writeUnlock();
		}
	}

	@Override
	public IServer removeServer( IServer server ) throws ServerInUseException
	{
		if (server == null) return null;

		serversLock.writeLock();
		try
		{
			// set the domain
			if ( !server.setDomain(null) )
				throw new ServerInUseException("The connector can not be removed because are in use by the domain");
			// remove the server by name
			return servers.remove(name);
		} finally
		{
			serversLock.writeUnlock();
		}
	}

	@Override
	public IServer removeServer( String name ) throws ServerInUseException
	{
		if (name == null) return null;

		return removeServer( getServer(name) );
	}
	
	@Override
	public void connectorRequestReceived( NetworkConnector<?, ?> connector,
		RequestEvent<?, ?> event ) throws BindletException, IOException
	{
		serversLock.readLock();
		try
		{
			// itera entre os servidores
			for (Map.Entry<String, IServer> entry : servers.entrySet())
			{
				IServer server = entry.getValue();
				if (server == null) continue;
				
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
