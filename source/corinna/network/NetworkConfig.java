package corinna.network;

import java.net.InetSocketAddress;

// TODO: rename to 'ConnectorConfig'
public class NetworkConfig
{

	public static final int DEFAULT_MAX_WORKERS = 8;

	private static final int MIN_WORKERS = 1;

	private static final int MAX_WORKERS = 1000;
	
	private InetSocketAddress address;
	
	private String name;
	
	private int maxWorkers = DEFAULT_MAX_WORKERS;
	
	public NetworkConfig( String name, String host, int port )
	{
		if (name == null || name.isEmpty())
			throw new NullPointerException("The connector name can not be null or empty");
		if (host == null || host.isEmpty())
			throw new NullPointerException("The host can not be null or empty");
		if (port < 0)
			throw new NullPointerException("The port must be a positive number");
		
		this.address = new InetSocketAddress(host, port);
		this.name = name;
	}
	
	public NetworkConfig( String name, InetSocketAddress address )
	{
		if (name == null || name.isEmpty())
			throw new NullPointerException("The connector name can not be null or empty");
		if (address == null)
			throw new NullPointerException("The address can not be null");
		
		this.address = address;
		this.name = name;
	}

	public void setName( String name )
	{
		if (name == null || name.isEmpty()) return;
		this.name = name;
	}

	public String getName()
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
		this.maxWorkers = maxWorkers;
	}

	public int getMaxWorkers()
	{
		return maxWorkers;
	}

}
