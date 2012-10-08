/*
 * Copyright 2011 Bruno Ribeiro
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

import javax.bindlet.exception.BindletException;

import corinna.exception.ConnectorInUseException;
import corinna.exception.LifecycleException;
import corinna.network.Connector;
import corinna.network.IConnector;
import corinna.network.IProtocol;
import corinna.network.RequestEvent;
import corinna.thread.ObjectLocker;


public class Server extends Lifecycle implements IServer
{

	private Map<String, IService> services;

	private ObjectLocker servicesLock;
	
	private IDomain domain = null;

	private IServerConfig config;

	private Map<String, IConnector> connectorsByName;
	
	private Map<String, List<IConnector>> connectorsByProtocol;
	
	private ObjectLocker connectorsLock;
	
	public Server( IServerConfig config )
	{
		if (config == null)
			throw new IllegalArgumentException("The context configuration can not be null");

		connectorsByName = new HashMap<String, IConnector>();
		connectorsByProtocol = new HashMap<String, List<IConnector>>();
		connectorsLock = new ObjectLocker();
		this.services = new HashMap<String, IService>();
		this.servicesLock = new ObjectLocker();
		this.config  = config;
	}

	@Override
	public boolean setDomain( IDomain domain )
	{
		if (domain != null && this.domain != null) return false;
		this.domain = domain;
		return true;
	}
	
	@Override
	public String getName()
	{
		return config.getServerName();
	}
	
	@Override
	public String[] getServiceNames()
	{
		servicesLock.readLock();
		try
		{
			return services.keySet().toArray( new String[0] );
		} finally
		{
			servicesLock.readUnlock();
		}		
	}

	@Override
	public IService getService( String name )
	{
		IService value = null;

		if (name == null) return null;

		servicesLock.readLock();
		try
		{
			value = services.get(name);
		} finally
		{
			servicesLock.readUnlock();
		}

		return value;
	}

	@Override
	public void addService( IService service )
	{
		if (service == null || service.getServer() != this) return;

		servicesLock.writeLock();
		try
		{
			services.put(service.getName(), service);
		} finally
		{
			servicesLock.writeUnlock();
		}
	}
	
	@Override
	public IService removeService( IService service )
	{
		if (service == null) return null;

		return removeService(service.getName());
	}

	@Override
	public IService removeService( String name )
	{
		IService value = null;

		if (name == null) return null;

		servicesLock.writeLock();
		try
		{
			value = services.remove(name);
		} finally
		{
			servicesLock.writeUnlock();
		}

		return value;
	}
	
	protected void initServices() throws LifecycleException
	{
		servicesLock.readLock();
		try
		{
			for (Map.Entry<String, IService> entry : services.entrySet())
				entry.getValue().init();
		} finally
		{
			servicesLock.readUnlock();
		}
	}
	
	@Override	
	public void onInit() throws LifecycleException
	{
		// TODO: introduce a rollover mechanism
		initServices();
		initNetworkConenctors();
	}

	protected void startServices() throws LifecycleException
	{
		servicesLock.readLock();
		try
		{
			for (Map.Entry<String, IService> entry : services.entrySet())
				entry.getValue().start();
		} finally
		{
			servicesLock.readUnlock();
		}
	}
	
	@Override
	public void onStart() throws LifecycleException
	{
		// TODO: introduce a rollover mechanism
		startServices();
		startNetworkConenctors();
	}

	protected void stopServices() throws LifecycleException
	{
		servicesLock.readLock();
		try
		{
			for (Map.Entry<String, IService> entry : services.entrySet())
				entry.getValue().stop();
		} finally
		{
			servicesLock.readUnlock();
		}
	}
	
	@Override
	public void onStop() throws LifecycleException
	{
		// TODO: introduce a rollover mechanism
		stopServices();
		stopNetworkConenctors();
	}

	protected void destroyServices() throws LifecycleException
	{
		servicesLock.readLock();
		try
		{
			for (Map.Entry<String, IService> entry : services.entrySet())
				entry.getValue().destroy();
		} finally
		{
			servicesLock.readUnlock();
		}
	}
	
	@Override
	public void onDestroy() throws LifecycleException
	{
		// TODO: introduce a rollover mechanism
		destroyServices();
		destroyNetworkConenctors();
	}

	@Override
	public void serviceEventReceived( IService service, ServiceEvent event )
	{
		if (event instanceof ServiceOwnerChangeEvent)
		{
			((ServiceOwnerChangeEvent)event).setResult(true);
			removeService(service);
		}
	}

	/**
	 * Dispatch an event to each registred service until the one handle it.
	 * 
	 * @param event
	 * @throws IOException 
	 * @throws BindletException 
	 */
	protected void dispatchEventToServices( RequestEvent<?,?> event ) throws BindletException, IOException
	{
		servicesLock.readLock();
		try
		{
			for (Map.Entry<String,IService> entry : services.entrySet())
			{
				entry.getValue().serverRequestReceived(this, event);
				if ( event.isHandled() ) break;
			}
		} finally
		{
			servicesLock.readUnlock();
		}
	}

	@Override
	public IDomain getDomain()
	{
		return domain;
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("  Server '" + getName() + "'\n");
		
		servicesLock.readLock();
		try
		{
			for (Map.Entry<String,IService> entry : services.entrySet())
				sb.append( entry.getValue() );
		} finally
		{
			servicesLock.readUnlock();
		}
		
		return sb.toString();
	}

	
	@Override
	public IServerConfig getConfig()
	{
		return config;
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
	public IConnector getConnector( IProtocol protocol, int index )
	{
		if (index < 0 || protocol == null) return null;

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
			if ( !connector.setServer(this) )
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

		if (connector.getServer() != this) return;
		
		connectorsLock.writeLock();
		try
		{
			// set the domain
			if ( !connector.setServer(null) )
				throw new ConnectorInUseException("The connector can not be removed because are in use by the domain");
			// remove connector by name
			connectorsByName.remove( connector.getName() );
			// remove connector by protocol
			List<IConnector> connectorList = connectorsByProtocol.get( connector.getProtocol().toString() );
			if (connectorList != null) connectorList.remove(connector);
			// unset the domain
			connector.setServer(null);
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
	public void removeAllConnectors( IProtocol protocol )
	{

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
	
	@Override
	public void connectorRequestReceived( Connector connector,
		RequestEvent<?, ?> event ) throws BindletException, IOException
	{
		dispatchEventToServices(event);
	}
	
}
