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

import java.net.InetSocketAddress;

import corinna.core.BasicConfig;

public class ConnectorConfig extends BasicConfig implements IConnectorConfig
{
	
	/**
	 * Default number of the worker threads that will be created for the network connector.
	 */
	public static final int DEFAULT_MAX_WORKERS = 150;

	/**
	 * Minimum number of the worker threads allowed.
	 */
	private static final int MIN_WORKERS = 1;

	/**
	 * Maximum number of the worker thread allowed.
	 */
	private static final int MAX_WORKERS = 1000;

	/**
	 * Parameter name to define the maximum number of the worker threads for the connector.
	 */
	public static final String PARAMETER_MAXWORKERS = "network.maxWorkerThreads";
	
	/**
	 * Address and port which the conenctor will listen for requests.
	 */
	private InetSocketAddress address;
	
	public ConnectorConfig( String name, String hostname, int port )
	{
		super(name);
		if (hostname == null || hostname.isEmpty())
			throw new NullPointerException("The hostname can not be null or empty");
		if (port < 0)
			throw new NullPointerException("The port must be a positive number");
		
		this.address = new InetSocketAddress(hostname, port);
		this.name = name;
	}
	
	public ConnectorConfig( String name, InetSocketAddress address )
	{
		super(name);
		if (address == null)
			throw new NullPointerException("The address can not be null");
		
		this.address = address;
		this.name = name;
	}

	public String getConnectorName()
	{
		return name;
	}
	
	public void setAddress( InetSocketAddress address )
	{
		if (address == null) return;
		this.address = address;
	}

	public InetSocketAddress getAddress()
	{
		return address;
	}

	public void setMaxWorkers( int maxWorkers )
	{
		if (maxWorkers < MIN_WORKERS)
			maxWorkers = MIN_WORKERS;
		else
		if (maxWorkers > MAX_WORKERS)
			maxWorkers = MAX_WORKERS;
		setParameter(PARAMETER_MAXWORKERS, String.valueOf(maxWorkers));
	}

	public int getMaxWorkers()
	{
		String value = getParameter(PARAMETER_MAXWORKERS, null);
		return stringToInt(value, DEFAULT_MAX_WORKERS);
	}

	public int getPort()
	{
		return address.getPort();
	}

	public String getHostName()
	{
		return address.getHostName();
	}

	protected int stringToInt( String value, int defaultValue )
	{
		if (value == null || value.isEmpty()) return defaultValue;
		
		try
		{
			return Integer.parseInt(value);
		} catch (Exception e)
		{
			return defaultValue;
		}
	}
}
