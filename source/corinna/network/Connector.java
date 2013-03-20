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
package corinna.network;


import java.io.IOException;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.bindlet.exception.BindletException;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import corinna.core.IServer;
import corinna.core.Lifecycle;
import corinna.exception.LifecycleException;
import corinna.thread.ObjectLocker;


/**
 * <p>
 * Define an abstract connector that must be specialized to create protocol-specific connectors,
 * such as <code>HttpConnector</code>. A connector is responsible to listen a TCP or UDP port and
 * delivery every incoming messages from clients to the bindlet container.
 * </p>
 * 
 * <h1>Custom parameters</h1>
 * 
 * <p>
 * This connector support a set of parameters through which it's possible to customize the connector
 * behavior. The following list show all supported parameters for this implementation:
 * </p>
 * 
 * <ul>
 * <li><strong>network.maxWorkerThreads:</strong> maximum number of the worker threads for the
 * connector.</li>
 * </ul>
 * 
 * @author Bruno Ribeiro
 */
public abstract class Connector extends Lifecycle implements IConnector, ChannelPipelineFactory,
	IStreamHandlerListener
{

	private ServerBootstrap bootstrap;

	private IConnectorConfig config;

	private IServer server = null;

	private ObjectLocker serverLock;

	private Channel channel = null;

	private Map<String, String> params;

	public Connector( IConnectorConfig config )
	{
		if (config == null)
			throw new IllegalArgumentException("The network configuration can not be null");

		this.config = config;
		this.params = new HashMap<String, String>();

		// create the bootstrap to listen on the given hostname and port
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
			Executors.newCachedThreadPool(), config.getMaxWorkers());
		this.bootstrap = new ServerBootstrap(factory);
		this.bootstrap.setPipelineFactory(this);

		this.serverLock = new ObjectLocker();
	}

	@Override
	public SocketAddress getAddress()
	{
		return config.getAddress();
	}

	@Override
	public IServer getServer()
	{
		serverLock.readLock();
		try
		{
			return server;
		} finally
		{
			serverLock.readUnlock();
		}
	}

	@Override
	public boolean setServer( IServer server )
	{
		serverLock.writeLock();
		try
		{
			if (server != null && this.server != null) return false;
			this.server = server;
			return true;
		} finally
		{
			serverLock.writeUnlock();
		}
	}

	/**
	 * <p>
	 * Dispatch the given request event to the server associated with the connector.
	 * </p>
	 * 
	 * @param event
	 * @throws BindletException
	 * @throws IOException
	 */
	protected void dispatchEventToServer( RequestEvent<?, ?> event ) throws BindletException,
		IOException
	{
		if (event == null || server == null) return;

		serverLock.readLock();
		try
		{
			server.connectorRequestReceived(this, event);
		} finally
		{
			serverLock.readUnlock();
		}
	}

	@Override
	public void handlerRequestReceived( StreamHandler handler, RequestEvent<?, ?> event,
		Channel channel ) throws BindletException, IOException
	{
		dispatchEventToServer(event);
	}

	protected void startConnector() throws LifecycleException
	{
		try
		{
			if (getServer() == null) throw new NullPointerException("Invalid domain");
			this.channel = bootstrap.bind(config.getAddress());
		} catch (Exception e)
		{
			throw new LifecycleException("Error starting component", e);
		}
	}

	@Override
	public void start() throws LifecycleException
	{
		super.start();
		startConnector();
	}

	@Override
	public void stop() throws LifecycleException
	{
		super.stop();
		stopConnector();
	}

	protected void stopConnector() throws LifecycleException
	{
		try
		{
			if (this.channel == null) return;
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

	protected IConnectorConfig getConfig()
	{
		return config;
	}

}
