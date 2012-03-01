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

import corinna.exception.BindletException;
import corinna.exception.ConnectorInUseException;
import corinna.exception.LifecycleException;
import corinna.exception.ServerInUseException;
import corinna.network.Connector;
import corinna.network.IConnector;
import corinna.network.IProtocol;
import corinna.network.RequestEvent;
import corinna.thread.ObjectLocker;


public final class Domain extends Lifecycle implements IDomain
{
	
	private String name;
	
	private ObjectLocker connectorsLock;

	private ObjectLocker serversLock;
	
	private Map<String, IConnector> connectorsByName;
	
	private Map<String, List<IConnector>> connectorsByProtocol;
	
	private Map<String, IServer> servers;
	
	public Domain( String name )
	{
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("The domain name can not be null or empty");
		this.name = name;

		connectorsByName = new HashMap<String, IConnector>();
		connectorsByProtocol = new HashMap<String, List<IConnector>>();
		servers = new HashMap<String, IServer>();
		connectorsLock = new ObjectLocker();
		serversLock = new ObjectLocker();
	}
	
	@Override
	public IConnector getConnector( String name )
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
	public IConnector getConnector( IProtocol<?, ?> protocol, int index )
	{
		if (name == null || name.isEmpty()) return null;

		connectorsLock.readLock();
		try
		{
			List<IConnector> list = connectorsByProtocol.get(protocol.toString());
			if (list == null || list.size() <= index) return null;
			return list.get(index);
		} finally
		{
			connectorsLock.readUnlock();
		}
	}

	@Override
	public void addConnector( IConnector connector ) throws ConnectorInUseException
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
			List<IConnector> list = connectorsByProtocol.get(protocol);
			if (list == null) 
			{
				list = new LinkedList<IConnector>();
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
	public void removeConnector( IConnector connector ) throws ConnectorInUseException
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
			List<IConnector> connectorList = connectorsByProtocol.get( connector.getProtocol().toString() );
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
	public void connectorRequestReceived( Connector connector,
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
				
				server.domainRequestReceived(this, event);
				if ( event.isHandled() ) break;
			}
		} finally
		{
			serversLock.readUnlock();
		}
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Domain '" + getName() + "'\n");
		serversLock.readLock();
		try
		{
			// itera entre os servidores
			for (Map.Entry<String, IServer> entry : servers.entrySet())
			{
				IServer server = entry.getValue();
				sb.append( server.toString() );
			}
		} finally
		{
			serversLock.readUnlock();
		}
		
		return sb.toString();
	}

	protected void startNetworkConenctors() throws LifecycleException
	{
		connectorsLock.readLock();
		try
		{
			// itera entre os servidores
			for (Map.Entry<String, IConnector> entry : connectorsByName.entrySet())
				entry.getValue().start();
		} finally
		{
			connectorsLock.readUnlock();
		}
	}
	
	protected void startServers() throws LifecycleException
	{
		serversLock.readLock();
		try
		{
			// itera entre os servidores
			for (Map.Entry<String, IServer> entry : servers.entrySet())
				entry.getValue().start();
		} finally
		{
			serversLock.readUnlock();
		}
	}
	
	@Override
	public void onStart() throws LifecycleException
	{
		startServers();
		startNetworkConenctors();
	}

	protected void stopNetworkConenctors() throws LifecycleException
	{
		connectorsLock.readLock();
		try
		{
			// itera entre os servidores
			for (Map.Entry<String, IConnector> entry : connectorsByName.entrySet())
				entry.getValue().stop();
		} finally
		{
			connectorsLock.readUnlock();
		}
	}
	
	protected void stopServers() throws LifecycleException
	{
		serversLock.readLock();
		try
		{
			// itera entre os servidores
			for (Map.Entry<String, IServer> entry : servers.entrySet())
				entry.getValue().stop();
		} finally
		{
			serversLock.readUnlock();
		}
	}
	
	@Override
	public void onStop() throws LifecycleException
	{
		stopServers();
		stopNetworkConenctors();
	}
	
	protected void initNetworkConenctors() throws LifecycleException
	{
		connectorsLock.readLock();
		try
		{
			// itera entre os servidores
			for (Map.Entry<String, IConnector> entry : connectorsByName.entrySet())
				entry.getValue().init();
		} finally
		{
			connectorsLock.readUnlock();
		}
	}
	
	protected void initServers() throws LifecycleException
	{
		serversLock.readLock();
		try
		{
			// itera entre os servidores
			for (Map.Entry<String, IServer> entry : servers.entrySet())
				entry.getValue().init();
		} finally
		{
			serversLock.readUnlock();
		}
	}
	
	@Override
	public void onInit() throws LifecycleException
	{
		initServers();
		initNetworkConenctors();
	}

	protected void destroyNetworkConenctors() throws LifecycleException
	{
		connectorsLock.readLock();
		try
		{
			// itera entre os servidores
			for (Map.Entry<String, IConnector> entry : connectorsByName.entrySet())
				entry.getValue().destroy();
		} finally
		{
			connectorsLock.readUnlock();
		}
	}
	
	protected void destroyServers() throws LifecycleException
	{
		serversLock.readLock();
		try
		{
			// itera entre os servidores
			for (Map.Entry<String, IServer> entry : servers.entrySet())
				entry.getValue().destroy();
		} finally
		{
			serversLock.readUnlock();
		}
	}
	
	@Override
	public void onDestroy() throws LifecycleException
	{
		destroyServers();
		destroyNetworkConenctors();
	}
	
}
