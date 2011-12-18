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

import org.apache.log4j.Logger;

import corinna.exception.BindletException;
import corinna.network.RequestEvent;
import corinna.thread.ObjectLocker;
import corinna.util.Reflection;


public abstract class Service implements IService
{

	private Logger log = Logger.getLogger(Service.class);
	
	private Map<String, IContext<?,?>> contexts;

	private ObjectLocker contextsLock;
	
	private String name;

	private IServer server;
	
	public Service( String name, IServer server )
	{
		if (name == null)
			throw new NullPointerException("A service name must be specified");
		
		this.name = name;
		this.contexts = new HashMap<String, IContext<?,?>>();
		this.contextsLock = new ObjectLocker();
		this.server = null;
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
		if (log.isDebugEnabled())
			log.debug("Dispatching event " + event.toString());
		
		contextsLock.readLock();
		try
		{
			// TODO: criar um "map" indexando por tipo de requisição
			for (Map.Entry<String,IContext<?, ?>> entry : contexts.entrySet())
			{
				// verifica se o contexto atual aceita o tipo de requisição recebida
				IContext<?, ?> context = entry.getValue();
				Class<?> contextRequest = Reflection.getGenericParameter(context, IContext.class, 0);
				Class<?> contextResponse = Reflection.getGenericParameter(context, IContext.class, 1);
				
				Class<?> requestType = Reflection.getGenericParameter(event, RequestEvent.class, 0);
				Class<?> responseType = Reflection.getGenericParameter(event, RequestEvent.class, 1);
				
				if (requestType.equals(contextRequest) && responseType.equals(contextResponse))
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
	public void init()
	{
		// does nothing		
	}

	@Override
	public void start()
	{
		// does nothing
	}

	@Override
	public void stop()
	{
		// does nothing
	}

	@Override
	public void destroy()
	{
		// does nothing
	}
	
}
