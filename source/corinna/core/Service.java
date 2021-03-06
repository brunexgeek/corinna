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

package corinna.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.bindlet.IBindletService;
import javax.bindlet.exception.BindletException;

import corinna.bindlet.BindletService;
import corinna.exception.LifecycleException;
import corinna.network.RequestEvent;
import corinna.thread.ObjectLocker;


public abstract class Service extends Lifecycle implements IService
{
	
	private Map<String, IContext<?,?>> contextsByName;

	private List<IContext<?,?>> contextsByOrder;
	
	private ObjectLocker contextsLock;

	private IServer server = null;

	private IServiceConfig config = null;
	
	public Service( IServiceConfig config, IServer server )
	{
		if (server == null)
			throw new IllegalArgumentException("The server instance can not be null");

		this.contextsByName = new HashMap<String, IContext<?,?>>();
		this.contextsByOrder = new LinkedList<IContext<?,?>>();
		this.contextsLock = new ObjectLocker();
		this.server = server;
		this.config  = config;
	}
	
	@Override
	public String getName()
	{
		return config.getServiceName();
	}

	@Override
	public IServer getServer()
	{
		return server;
	}

	/*@Override
	public boolean setServer( IServer server )
	{
		serverLock.writeLock();
		try
		{
			// dispara um evento ao servidor atualmente associado pedindo permissão para
			// utilizar o serviço
			if (this.server != null)
			{
				ServiceOwnerChangeEvent e = new ServiceOwnerChangeEvent(ServiceEventType.OWNER_CHANGE);
				this.server.serviceEventReceived(this, e);
				if (!e.getResult()) return false;
			}
			this.server = server;
			return true;
		} finally
		{
			serverLock.writeUnlock();
		}
	}*/
	
	@Override
	public IContext<?,?> getContext( String name )
	{
		contextsLock.readLock();
		try
		{
			return contextsByName.get(name);
		} finally
		{
			contextsLock.readUnlock();
		}
	}

	@Override
	public void addContext( IContext<?,?> context )
	{
		if (context == null || context.getService() != this) return;
		
		contextsLock.writeLock();
		try
		{
			contextsByName.put(context.getName(), context);
			contextsByOrder.add(context);
		} finally
		{
			contextsLock.writeUnlock();
		}
	}

	@Override
	public IContext<?,?> removeContext( IContext<?,?> context )
	{
		return removeContext(context.getName());
	}

	@Override
	public IContext<?,?> removeContext( String name )
	{
		contextsLock.writeLock();
		try
		{
			IContext<?,?> ctx = contextsByName.remove(name);
			if (ctx != null) contextsByOrder.remove(ctx);
			return ctx;
		} finally
		{
			contextsLock.writeUnlock();
		}
	}
	
	/**
	 * Dispatch an event to each registred services until the one handle it.
	 * 
	 * @param event
	 * @throws IOException 
	 * @throws BindletException 
	 */
	protected void dispatchEventToContexts( RequestEvent<?,?> event ) throws BindletException, IOException
	{
		contextsLock.readLock();
		try
		{
			for (IContext<?, ?> context : contextsByOrder)
			{
				// get the request and response types accepted by the current context
				Class<?> contextRequest = context.getRequestType();
				Class<?> contextResponse = context.getResponseType();
				// get the request and response types of the current event
				Class<?> requestType = event.getRequestType();
				Class<?> responseType = event.getResponseType();
				// check if the types are compatible
				if ( contextRequest.isAssignableFrom(requestType) &&
					contextResponse.isAssignableFrom(responseType) )
				{
					context.serviceRequestReceived(this, event);
					if ( event.isHandled() ) break;
				}
			}
		} finally
		{
			contextsLock.readUnlock();
		}
	}
	
	@Override
	public void serverRequestReceived( IServer server, RequestEvent<?,?> event ) 
		throws BindletException, IOException
	{
		dispatchEventToContexts(event);
	}

	@Override
	public IBindletService getBindletService()
	{
		return new BindletService(this);
	}
		
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("    Service '" + getName() + "'\n");
		
		contextsLock.readLock();
		try
		{
			for (Map.Entry<String,IContext<?,?>> entry : contextsByName.entrySet())
				sb.append( entry.getValue() );
		} finally
		{
			contextsLock.readUnlock();
		}
		
		return sb.toString();
	}

	protected void startContexts() throws LifecycleException
	{
		contextsLock.readLock();
		try
		{
			// itera entre os servidores
			for (Map.Entry<String,IContext<?,?>> entry : contextsByName.entrySet())
				entry.getValue().start();
		} finally
		{
			contextsLock.readUnlock();
		}
	}
	
	@Override
	public void onStart() throws LifecycleException
	{
		startContexts();
	}

	protected void stopContexts() throws LifecycleException
	{
		contextsLock.readLock();
		try
		{
			// itera entre os servidores
			for (Map.Entry<String,IContext<?,?>> entry : contextsByName.entrySet())
				entry.getValue().stop();
		} finally
		{
			contextsLock.readUnlock();
		}
	}
	
	@Override
	public void onStop() throws LifecycleException
	{
		stopContexts();
	}
	
	protected void initContexts() throws LifecycleException
	{
		contextsLock.readLock();
		try
		{
			// itera entre os servidores
			for (Map.Entry<String,IContext<?,?>> entry : contextsByName.entrySet())
				entry.getValue().init();
		} finally
		{
			contextsLock.readUnlock();
		}
	}
	
	@Override
	public void onInit() throws LifecycleException
	{
		initContexts();
	}

	protected void destroyContexts() throws LifecycleException
	{
		contextsLock.readLock();
		try
		{
			// itera entre os servidores
			for (Map.Entry<String,IContext<?,?>> entry : contextsByName.entrySet())
				entry.getValue().destroy();
		} finally
		{
			contextsLock.readUnlock();
		}
	}
	
	@Override
	public void onDestroy() throws LifecycleException
	{
		destroyContexts();
	}
	
	
	@Override
	public String[] getContextNames()
	{
		contextsLock.readLock();
		
		String names[] = new String[contextsByName.size()];
		int i = 0;
		
		try
		{
			// itera entre os servidores
			for (Map.Entry<String,IContext<?,?>> entry : contextsByName.entrySet())
			{
				names[i] = entry.getValue().getName();
				++i;
			}
		} finally
		{
			contextsLock.readUnlock();
		}
		
		return names;
	}
	
	
}
