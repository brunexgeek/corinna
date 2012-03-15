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
import java.util.Map;

import javax.bindlet.exception.BindletException;

import corinna.exception.LifecycleException;
import corinna.network.RequestEvent;
import corinna.thread.ObjectLocker;


public class Server extends Lifecycle implements IServer
{

	private Map<String, IService> services;

	private ObjectLocker servicesLock;
	
	private IDomain domain = null;

	private IServerConfig config;

	public Server( IServerConfig config )
	{
		if (config == null)
			throw new IllegalArgumentException("The context configuration can not be null");
		
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
		initServices();
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
		startServices();
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
		stopServices();
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
		destroyServices();
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
	 * Dispatch an event to each registred services until the one handle it.
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
	public void domainRequestReceived( IDomain domain, RequestEvent<?, ?> event ) throws 
		BindletException, IOException
	{
		dispatchEventToServices(event);
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
	
}
