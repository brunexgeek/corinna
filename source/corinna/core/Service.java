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

import javax.bindlet.IBindletService;

import corinna.bindlet.BindletService;
import corinna.exception.BindletException;
import corinna.exception.LifecycleException;
import corinna.network.RequestEvent;
import corinna.thread.ObjectLocker;
import corinna.util.Reflection;
import corinna.util.conf.ISection;
import corinna.util.conf.Section;


public abstract class Service extends Lifecycle implements IService
{
	
	private Map<String, IContext<?,?>> contexts;

	private ObjectLocker contextsLock;
	
	private String name;

	private IServer server = null;

	private ISection config = null;
	
	public Service( String name, IServer server, ISection config )
	{
		if (name == null)
			throw new IllegalArgumentException("A service name must be specified");
		if (server == null)
			throw new IllegalArgumentException("The server instance can not be null");
		
		this.name = name;
		this.contexts = new HashMap<String, IContext<?,?>>();
		this.contextsLock = new ObjectLocker();
		this.server = server;
		this.config  = (config == null) ? new Section("Parameters") : config;
	}
	
	@Override
	public String getName()
	{
		return name;
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
			return contexts.get(name);
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
			contexts.put(context.getName(), context);
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
			return contexts.remove(name);
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
			// TODO: criar um "map" indexando por tipo de requisição
			for (Map.Entry<String,IContext<?, ?>> entry : contexts.entrySet())
			{
				// get the request and response types accepted by the current context
				IContext<?, ?> context = entry.getValue();
				Class<?> contextRequest = context.getRequestType();//Reflection.getGenericParameter(context, IContext.class, 0);
				Class<?> contextResponse = context.getResponseType();//Reflection.getGenericParameter(context, IContext.class, 1);
				// get the request and response types of the current event
				Class<?> requestType = null;
				Class<?> responseType = null;
				if (event.getRequest() != null)
					requestType = event.getRequest().getClass();
				else
					requestType = Reflection.getGenericParameter(event, RequestEvent.class, 0);
				if (event.getResponse() != null)
					responseType = event.getResponse().getClass();
				else
					responseType = Reflection.getGenericParameter(event, RequestEvent.class, 1);
				// check if the types are compatible
				boolean validReq = contextRequest.isAssignableFrom(requestType);
				boolean validRes = contextResponse.isAssignableFrom(responseType);
				
				if (validReq && validRes)
				{
					entry.getValue().serviceRequestReceived(this, event);
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
	public String getParameter( String name )
	{
		return config.getValue(name, null);
	}

	@Override
	public void setParameter( String name, String value )
	{
		config.setValue(name, value);
	}

	@Override
	public String[] getParameterNames()
	{
		return config.getKeys();
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("    Service '" + getName() + "'\n");
		
		contextsLock.readLock();
		try
		{
			for (Map.Entry<String,IContext<?,?>> entry : contexts.entrySet())
				sb.append( entry.getValue() );
		} finally
		{
			contextsLock.readUnlock();
		}
		
		return sb.toString();
	}

	@Override
	public void onStart() throws LifecycleException
	{
		contextsLock.readLock();
		try
		{
			// itera entre os servidores
			for (Map.Entry<String,IContext<?,?>> entry : contexts.entrySet())
				entry.getValue().start();
		} finally
		{
			contextsLock.readUnlock();
		}
	}

	@Override
	public void onStop() throws LifecycleException
	{
		contextsLock.readLock();
		try
		{
			// itera entre os servidores
			for (Map.Entry<String,IContext<?,?>> entry : contexts.entrySet())
				entry.getValue().stop();
		} finally
		{
			contextsLock.readUnlock();
		}
	}
	
	@Override
	public void onInit() throws LifecycleException
	{
		contextsLock.readLock();
		try
		{
			// itera entre os servidores
			for (Map.Entry<String,IContext<?,?>> entry : contexts.entrySet())
				entry.getValue().init();
		} finally
		{
			contextsLock.readUnlock();
		}
	}

	@Override
	public void onDestroy() throws LifecycleException
	{
		contextsLock.readLock();
		try
		{
			// itera entre os servidores
			for (Map.Entry<String,IContext<?,?>> entry : contexts.entrySet())
				entry.getValue().destroy();
		} finally
		{
			contextsLock.readUnlock();
		}
	}
	
}
