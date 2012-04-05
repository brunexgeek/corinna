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

import javax.bindlet.IBindlet;
import javax.bindlet.IBindletContext;
import javax.bindlet.exception.BindletException;

import corinna.network.IAdapter;
import corinna.network.RequestEvent;
import corinna.thread.ObjectLocker;


public abstract class Context<R, P> extends Lifecycle implements IContext<R, P>
{

	private IBindletContext bindletContext;

	protected Map<String, IBindletRegistration> repos;

	protected ObjectLocker reposLock;

	private IService service = null;

	private Boolean updateReposArray = false;

	private IBindletRegistration[] reposArray = null;

	private IContextConfig config = null;

	private Map<String, IAdapter> adapters;
	
	private ObjectLocker adaptersLock;
	
	public Context( IContextConfig config, IService service )
	{
		if (config == null)
			throw new IllegalArgumentException("The context configuration can not be null");
		if (service == null) 
			throw new IllegalArgumentException("The service instance can not be null");

		this.bindletContext = createBindletContext();
		this.repos = new HashMap<String, IBindletRegistration>();
		this.reposLock = new ObjectLocker();
		this.config = config;
		this.service = service;
		this.adapters = new HashMap<String, IAdapter>();
		this.adaptersLock = new ObjectLocker();
	}

	protected abstract IBindletContext createBindletContext();

	@Override
	public String getName()
	{
		return config.getContextName();
	}

	@Override
	public IService getService()
	{
		return service;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void serviceRequestReceived( IService service, RequestEvent<?, ?> event )
		throws BindletException, IOException
	{
		if (getLifecycleState() != LifecycleState.STARTED) return;
				
		R request = null;
		P response = null;

		try
		{
			// NOTE: the service garantees compatibility for request/response, but we don't trust :)
			request = (R) event.getRequest();
			response = (P) event.getResponse();
		} catch (Exception e)
		{
			throw new BindletException("Incompatible bindlet request for this context", e);
		}

		if (acceptRequest(request))
		{
			event.setHandled(true);
			boolean result = dispatchEventToBindlet(request, response);
			event.setHandled(result);
		}
	}

	protected abstract boolean acceptRequest( R request );

	@SuppressWarnings("unchecked")
	protected boolean dispatchEventToBindlet( R request, P response ) throws BindletException,
		IOException
	{
		// find the registration of the bindlet that must process this request
		IBindletRegistration reg = getBindletRegistration(request);
		if (reg == null) return false;
		IBindlet<R, P> bindlet = (IBindlet<R, P>) reg.createBindlet();
		// process the request event
		bindlet.init(reg.getBindletConfig());
		bindlet.process(request, response);
		bindlet.destroy();
		reg.releaseBindlet(bindlet);
		return true;
	}

	@Override
	public IBindletContext getBindletContext()
	{
		return bindletContext;
	}

	@Override
	public IBindletRegistration addBindlet( String bindletName, String bindletClassName )
		throws BindletException, ClassNotFoundException
	{
		IBindletRegistration reg = new BindletRegistration(this, bindletName, bindletClassName);

		reposLock.writeLock();
		try
		{
			repos.put(bindletName, reg);
			updateReposArray = true;
			return reg;
		} finally
		{
			reposLock.writeUnlock();
		}
	}

	@Override
	public IBindletRegistration addBindlet( String bindletName,
		Class<? extends IBindlet<R, P>> bindletClass ) throws BindletException
	{
		try
		{
			IBindletRegistration reg = addBindlet(bindletName, bindletClass.getName());
			updateReposArray = true;
			return reg;
		} catch (ClassNotFoundException e)
		{
			// supress any 'ClassNotFound' errors (because they will never occur)
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public IBindlet<R, P> createBindlet( String bindletName ) throws BindletException
	{
		reposLock.readLock();
		try
		{
			IBindletRegistration reg = repos.get(bindletName);
			if (reg == null)
				throw new BindletException(
					"There are not a bindlet registred with the specified name");
			return (IBindlet<R, P>) reg.createBindlet();
		} finally
		{
			reposLock.readUnlock();
		}
	}

	@Override
	public boolean containsBindlet( String bindletName )
	{
		reposLock.readLock();
		try
		{
			return repos.containsKey(bindletName);
		} finally
		{
			reposLock.readUnlock();
		}
	}

	@Override
	public IContextConfig getConfig()
	{
		return config;
	}
	
	/*protected boolean isModified()
	{
		synchronized (isModified)
		{
			return isModified;
		}
	}

	protected void setModified( boolean value )
	{
		synchronized (isModified)
		{
			isModified = value;
		}
	}*/

	@Override
	public IBindletRegistration getBindletRegistration( String name )
	{
		IBindletRegistration result;

		reposLock.readLock();
		result = repos.get(name);
		reposLock.readUnlock();

		return result;
	}

	@Override
	public IBindletRegistration[] getBindletRegistrations()
	{
		IBindletRegistration[] result = null;

		reposLock.readLock();
		if (reposArray == null || updateReposArray)
		{
			reposLock.readUnlock();
			reposLock.writeLock();
			reposArray = repos.values().toArray(new IBindletRegistration[0]);
			result = reposArray;
			updateReposArray = false;
			reposLock.writeUnlock();
		}
		else
		{
			result = reposArray;
			reposLock.readUnlock();
		}

		return result;
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("      Context '" + getName() + "'\n");
		
		reposLock.readLock();
		try
		{
			for (Map.Entry<String,IBindletRegistration> entry : repos.entrySet())
				sb.append( entry.getValue() );
		} finally
		{
			reposLock.readUnlock();
		}
		
		return sb.toString();
	}

	@Override
	public void addAdapter( IAdapter adapter )
	{
		if (adapter == null) return;
		
		adaptersLock.readLock();
		try
		{
			adapters.put(adapter.getName(), adapter);
		} finally
		{
			adaptersLock.readUnlock();
		}
	}
	
	@Override
	public void removeAdapter( String name )
	{
		if (name == null || name.isEmpty()) return;
		
		adaptersLock.readLock();
		try
		{
			adapters.remove(name);
		} finally
		{
			adaptersLock.readUnlock();
		}
	}
	
}
