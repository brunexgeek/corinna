/*
 * Copyright 2011-2012 Bruno Ribeiro
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

import java.util.HashMap;
import java.util.Map;

import corinna.exception.LifecycleException;
import corinna.exception.ServerInUseException;
import corinna.thread.ObjectLocker;


public final class Domain extends Lifecycle implements IDomain
{
	
	private String name;

	private ObjectLocker serversLock;
	
	private Map<String, IServer> servers;
	
	public Domain( String name )
	{
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("The domain name can not be null or empty");
		this.name = name;

		servers = new HashMap<String, IServer>();
		serversLock = new ObjectLocker();
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
				throw new ServerInUseException("The server can not be removed because are in use by another domain");
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
				throw new ServerInUseException("The server can not be removed because are in use by the domain");
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
	}
	
	protected void destroyServers() throws LifecycleException
	{
		serversLock.readLock();
		try
		{
			// itera entre os servidores
			for (Map.Entry<String, IServer> entry : servers.entrySet())
			{
				IServer current = entry.getValue();
				if (!current.getLifecycleState().isDestroying())
					current.destroy();
			}
		} finally
		{
			serversLock.readUnlock();
		}
	}
	
	@Override
	public void onDestroy() throws LifecycleException
	{
		destroyServers();
	}

	@Override
	public String dumpHierarchy()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("\nDomain '" + getName() + "'\n");
		
		serversLock.readLock();
		try
		{
			for (Map.Entry<String, IServer> entry : servers.entrySet())
			{
				IServer server = entry.getValue();
				String services[] = server.getServiceNames();

				// iterate between the services
				for (String serviceName : services)
				{
					IService service = server.getService(serviceName);
					String contexts[] = service.getContextNames();
					
					// iterate between the contexts
					for (String contextName : contexts)
					{
						IContext<?,?> context = service.getContext(contextName);
						String bindlets[] = context.getBindletNames();
						
						// iterate between the bindlet registrations
						for (String bindletName : bindlets)
						{
							IBindletRegistration registry = context.getBindletRegistration(bindletName);
							
							/*sb.append("| ");
							sb.append( truncate(server.getName(), 20) );
							sb.append(" | ");
							sb.append( truncate(service.getName(), 20) );
							sb.append(" | ");
							sb.append( truncate(context.getName(), 20) );
							sb.append(" | ");
							sb.append( truncate(registry.getBindletName(), 20) );
							sb.append(" | ");
							sb.append( truncate(registry.getBindletClassName(), 40) );
							sb.append(" |\n");*/
							
							sb.append("   ");
							sb.append( registry.getBindletName() );
							sb.append(" [server='");
							sb.append( server.getName() );
							sb.append("'; service='");
							sb.append( service.getName() );
							sb.append("'; context='");
							sb.append( context.getName() );
							sb.append("'; class='");
							sb.append( registry.getBindletClassName() );
							sb.append("']\n");
						}
					}
				}
			}
		} finally
		{
			serversLock.readUnlock();
		}
		
		return sb.toString();
	}
	
	protected String truncate( String value, int length )
	{
		if (length <= 3) return "...";
		length -= 3;
		
		if (value.length() > length)
			return "..." + value.substring(value.length() - length, value.length());
		else
			return String.format("%-" + (length + 3) + "s", value);
	}
	
}
