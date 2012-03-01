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

package corinna.network;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import corinna.core.IDomain;
import corinna.core.Lifecycle;
import corinna.exception.AdapterException;
import corinna.exception.BindletException;
import corinna.exception.LifecycleException;
import corinna.thread.ObjectLocker;


/**
 * 
 * @author bruno
 *
 * @param <R> pipeline request type
 * @param <P> pipeline response type
 */
public abstract class Connector extends Lifecycle implements IConnector, 
	ChannelPipelineFactory, IStreamHandlerListener
{

	private ServerBootstrap bootstrap;

	private IConnectorConfig config;
	
	private IDomain domain = null;
	
	private ObjectLocker domainLock;
	
	private Channel channel = null;
	
	private Map<String,String> params;
	
	private Map<String, IAdapter> adapters;
	
	private ObjectLocker adaptersLock;
	
	public Connector( IConnectorConfig config )
	{
		if (config == null)
			throw new IllegalArgumentException("The network configuration can not be null");

		this.config = config;
		this.params = new HashMap<String,String>();
		this.adapters = new HashMap<String, IAdapter>();
	
		ChannelFactory factory = new NioServerSocketChannelFactory(
			Executors.newCachedThreadPool(), Executors.newCachedThreadPool(), config.getMaxWorkers());
		this.bootstrap = new ServerBootstrap(factory);
		this.bootstrap.setPipelineFactory(this);
		
		this.domainLock = new ObjectLocker();
		this.adaptersLock = new ObjectLocker();
	}

	@Override
	public SocketAddress getAddress()
	{
		return config.getAddress();
	}
	
	@Override
	public IDomain getDomain( )
	{
		domainLock.readLock();
		try
		{
			return domain;
		} finally
		{
			domainLock.readUnlock();
		}
	}
	
	@Override
	public boolean setDomain( IDomain domain )
	{
		domainLock.writeLock();
		try
		{
			if (domain != null && this.domain != null) return false;
			this.domain = domain;
			return true;
		} finally
		{
			domainLock.writeUnlock();
		}
	}
	
	protected void dispatchEventToDomain( RequestEvent<?,?> event ) throws BindletException, 
		IOException
	{
		if (event == null || domain == null) return;
		
		domainLock.readLock();
		try
		{
			domain.connectorRequestReceived(this, event);
		} finally
		{
			domainLock.readUnlock();
		}
	}
	
	public abstract IAdapter getDefaultAdapter();
	
	public IAdapter getAdapter( Object request, Object response )
	{
		if (request == null)
			throw new NullPointerException("The request can not be null");
		
		adaptersLock.readLock();
		try
		{
			for ( Map.Entry<String,IAdapter> entry : adapters.entrySet() )
			{
				IAdapter adapter = entry.getValue();
				if (adapter.isCompatibleWith(request, response))
					return adapter;
			}
		} finally
		{
			adaptersLock.readUnlock();
		}

		return getDefaultAdapter();
	}
	
	@Override
	public void handlerRequestReceived( StreamHandler handler, Object request, Object response, Channel channel ) 
		throws BindletException, IOException
	{
		RequestEvent<?, ?> event = null;
		IAdapter adapter = getAdapter(request, response);

		try
		{
			event = adapter.translate(request, response, channel);
			dispatchEventToDomain(event);
		} catch (AdapterException e)
		{
			throw new BindletException("Error translating request", e);
		} catch (Exception e)
		{
			adapter.onError(event, channel, e);
			return;
		}
		adapter.onSuccess(event, channel);
	}
	
	protected void startConnector() throws LifecycleException
	{
		try
		{
			if ( getDomain() == null )
				throw new NullPointerException("Invalid domain");
			this.channel = bootstrap.bind(config.getAddress());
		} catch (Exception e)
		{
			throw new LifecycleException("Error starting component", e);
		}
	}
	
	@Override
	public void start() throws LifecycleException
	{
		startConnector();
	}

	protected void stopConnector() throws LifecycleException
	{
		try
		{
			ChannelFuture future = this.channel.unbind();
			future.await();
			this.channel = null;
		} catch (Exception e)
		{
			throw new LifecycleException("Error stopping component", e);
		}
	}
	
	@Override
	public void onStop() throws LifecycleException
	{
		stopConnector();
	}
	
	@Override
	public String getName()
	{
		return config.getConnectorName();
	}

	@Override
	public String getParameter( String name )
	{
		if (name == null) return null;
		return params.get(name);
	}

	@Override
	public void setParameter( String name, String value )
	{
		if (name == null) return;
		if (value == null)
			params.remove(name);
		else
			params.put(name, value);
	}

	@Override
	public String[] getParameterNames()
	{
		return params.keySet().toArray(new String[0]);
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
