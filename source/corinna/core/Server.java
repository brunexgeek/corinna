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
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import corinna.core.LifecycleManager.StateTransition;
import corinna.exception.BindletException;
import corinna.exception.LifecycleException;
import corinna.network.RequestEvent;
import corinna.thread.ObjectLocker;


public class Server implements IServer
{

	private Logger log = Logger.getLogger(Server.class);
	
	private LifecycleManager lifecycle;

	private Map<String, IService> services;

	private ObjectLocker servicesLock;
	
	private String name;
	
	private IDomain domain = null;

	public Server( String name )
	{
		if (name == null)
			throw new IllegalArgumentException("The server name can not be null or empty");
		this.name = name;
		
		lifecycle = new LifecycleManager();
		services = new HashMap<String, IService>();
		servicesLock = new ObjectLocker();
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
		return name;
	}
	
	@Override
	public void addLifecycleListener( ILifecycleListener listener )
	{
		lifecycle.addLifecycleListener(listener);
	}

	@Override
	public void removeLifecycleListener( ILifecycleListener listener )
	{
		lifecycle.removeLifecycleListener(listener);
	}

	@Override
	public LifecycleState getLifecycleState()
	{
		return lifecycle.getLifecycleState();
	}

	@Override
	public String getLifecycleStateName()
	{
		return lifecycle.getLifecycleStateName();
	}

	@Override
	public List<IService> getServices()
	{
		return null;
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
		servicesLock.writeLock();
		try
		{
			if (service instanceof Service)
				((Service)service).setServer(this);
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
	
	public void initServices() throws LifecycleException
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

	public void startServices() throws LifecycleException
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

	public void stopServices() throws LifecycleException
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

	public void destroyServices() throws LifecycleException
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

	protected void initInternal() throws LifecycleException
	{
		// does nothing
	}

	@Override
	public void init() throws LifecycleException
	{
		StateTransition trans = lifecycle.changeLifecycleState(LifecycleState.INITIALIZING);
		if (trans == StateTransition.IGNORE) return;

		try
		{
			initInternal();
			initServices();
			lifecycle.changeLifecycleState(LifecycleState.INITIALIZED);
		} catch (Exception e)
		{
			lifecycle.setLifecycleState(LifecycleState.FAILED);
		}
	}

	protected void startInternal() throws LifecycleException
	{
		// does nothing
	}

	@Override
	public void start() throws LifecycleException
	{
		StateTransition trans = lifecycle.changeLifecycleState(LifecycleState.STARTING);
		if (trans == StateTransition.IGNORE) return;

		try
		{
			startInternal();
			startServices();
			lifecycle.changeLifecycleState(LifecycleState.STARTED);
		} catch (Exception e)
		{
			lifecycle.setLifecycleState(LifecycleState.FAILED);
		}
	}

	protected void stopInternal() throws LifecycleException
	{
		// does nothing
	}

	@Override
	public void stop() throws LifecycleException
	{
		StateTransition trans = lifecycle.changeLifecycleState(LifecycleState.STOPPING);
		if (trans == StateTransition.IGNORE) return;

		try
		{
			stopInternal();
			stopServices();
			lifecycle.changeLifecycleState(LifecycleState.STOPPED);
		} catch (Exception e)
		{
			lifecycle.setLifecycleState(LifecycleState.FAILED);
		}
	}

	protected void destroyInternal() throws LifecycleException
	{
		// does nothing
	}

	@Override
	public void destroy() throws LifecycleException
	{
		StateTransition trans = lifecycle.changeLifecycleState(LifecycleState.DESTROYING);
		if (trans == StateTransition.IGNORE) return;

		try
		{
			destroyInternal();
			destroyServices();
			lifecycle.changeLifecycleState(LifecycleState.DESTROYED);
		} catch (Exception e)
		{
			lifecycle.setLifecycleState(LifecycleState.FAILED);
		}
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
		//if (log.isDebugEnabled())
			log.debug("Dispatching event " + event.toString());
		
		servicesLock.readLock();
		try
		{
			//RequestEvent<R,P> e = new RequestEvent<R,P>(this, event.getRequest(), event.getResponse());
			for (Map.Entry<String,IService> entry : services.entrySet())
			{
				entry.getValue().serverRequestReceived(this, event);
				if ( event.isHandled() ) break;
			}
			//event.setHandled( e.isHandled() );
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
	
}
