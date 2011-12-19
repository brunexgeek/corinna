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
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.net.URL;
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
import corinna.core.LifecycleManager.StateTransition;
import corinna.core.LifecycleState;
import corinna.exception.BindletException;
import corinna.exception.LifecycleException;
import corinna.thread.ObjectLocker;


/**
 * 
 * @author bruno
 *
 * @param <R> bindlet request type
 * @param <P> bindlet resposne type
 */
public abstract class NetworkConnector<R,P> extends Lifecycle implements INetworkConnector<R,P>, 
	ChannelPipelineFactory, IStreamHandlerListener<R,P>
{

	private ServerBootstrap bootstrap;

	private SocketAddress address;
	
	private IDomain domain = null;
	
	private ObjectLocker domainLock;
	
	private Channel channel = null;
	
	private String name;
	
	private Map<String,String> params;

	public NetworkConnector( String name, String url ) throws MalformedURLException
	{
		this( name, new URL(url), Runtime.getRuntime().availableProcessors() * 2 );
	}

	public NetworkConnector( String name, String url, int workers ) throws MalformedURLException
	{
		this( name, new URL(url), workers );
	}
	
	public NetworkConnector( String name, URL address, int workers )
	{
		if (address == null) 
			throw new NullPointerException("The address is required");
		if (name == null)
			throw new IllegalArgumentException("The connector name can not be null or empty");

		this.address = new InetSocketAddress(address.getHost(), address.getPort());
		this.name = name;
		this.params = new HashMap<String,String>();
	
		ChannelFactory factory = new NioServerSocketChannelFactory(
			Executors.newCachedThreadPool(), Executors.newCachedThreadPool(), workers);
		this.bootstrap = new ServerBootstrap(factory);
		this.bootstrap.setPipelineFactory(this);
		
		this.domainLock = new ObjectLocker();
	}

	@Override
	public SocketAddress getAddress()
	{
		return address;
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
	
	protected void dispatchEventToDomain( RequestEvent<R,P> event ) throws BindletException, 
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
	
	@Override
	public void handlerRequestReceived( StreamHandler handler, RequestEvent<R,P> event ) 
		throws BindletException, IOException
	{
		dispatchEventToDomain(event);
	}
	
	@Override
	public void start() throws LifecycleException
	{
		StateTransition trans = lifecycle.changeLifecycleState(LifecycleState.STARTING);
		if (trans == StateTransition.IGNORE) return;

		try
		{
			if ( getDomain() == null )
				throw new NullPointerException("Invalid domain");
			startInternal();
			this.channel = bootstrap.bind(this.address);
			lifecycle.changeLifecycleState(LifecycleState.STARTED);
		} catch (Exception e)
		{
			lifecycle.setLifecycleState(LifecycleState.FAILED);
			throw new LifecycleException("Error starting component", e);
		}
	}

	@Override
	public void stop() throws LifecycleException
	{
		StateTransition trans = lifecycle.changeLifecycleState(LifecycleState.STOPPING);
		if (trans == StateTransition.IGNORE) return;

		try
		{
			ChannelFuture future = this.channel.unbind();
			future.await();
			this.channel = null;
			stopInternal();
			lifecycle.changeLifecycleState(LifecycleState.STOPPED);
		} catch (Exception e)
		{
			lifecycle.setLifecycleState(LifecycleState.FAILED);
			throw new LifecycleException("Error stopping component", e);
		}
	}
	
	@Override
	public String getName()
	{
		return name;
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
	protected void initInternal() throws LifecycleException
	{
		// does nothing
	}

	@Override
	protected void startInternal() throws LifecycleException
	{
		// does nothing
	}

	@Override
	protected void stopInternal() throws LifecycleException
	{
		// does nothing
	}

	@Override
	protected void destroyInternal() throws LifecycleException
	{
		// does nothing
	}
	
}
