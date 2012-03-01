//package corinna.core;
//
//import java.net.InetSocketAddress;
//
//
//
//public class NetworkConnectorConfig extends BasicConfig implements INetworkConnectorConfig
//{
//
//	public static final int DEFAULT_MAX_WORKERS = 8;
//
//	private static final int MIN_WORKERS = 1;
//
//	private static final int MAX_WORKERS = 1000;
//
//	private static final String PROP_MAX_WORKERS = "network.MaxWorkerThreads";
//	
//	private InetSocketAddress address;
//	
//	private String name;
//	
//	public NetworkConnectorConfig( String name, String hostname, int port )
//	{
//		super(name);
//		if (hostname == null || hostname.isEmpty())
//			throw new NullPointerException("The hostname can not be null or empty");
//		if (port < 0)
//			throw new NullPointerException("The port must be a positive number");
//		
//		this.address = new InetSocketAddress(hostname, port);
//		this.name = name;
//	}
//	
//	public NetworkConnectorConfig( String name, InetSocketAddress address )
//	{
//		super(name);
//		if (address == null)
//			throw new NullPointerException("The address can not be null");
//		
//		this.address = address;
//		this.name = name;
//	}
//
//	public String getName()
//	{
//		return name;
//	}
//	
//	public void setAddress( InetSocketAddress address )
//	{
//		if (address == null) return;
//		this.address = address;
//	}
//
//	public InetSocketAddress getAddress()
//	{
//		return address;
//	}
//
//	public void setMaxWorkers( int maxWorkers )
//	{
//		if (maxWorkers < MIN_WORKERS)
//			maxWorkers = MIN_WORKERS;
//		else
//		if (maxWorkers > MAX_WORKERS)
//			maxWorkers = MAX_WORKERS;
//		setParameter(PROP_MAX_WORKERS, String.valueOf(maxWorkers));
//	}
//
//	public int getMaxWorkers()
//	{
//		String value = getParameter(PROP_MAX_WORKERS, null);
//		return stringToInt(value, DEFAULT_MAX_WORKERS);
//	}
//
//	public int getPort()
//	{
//		return address.getPort();
//	}
//
//	public String getHostName()
//	{
//		return address.getHostName();
//	}
//
//	protected int stringToInt( String value, int defaultValue )
//	{
//		if (value == null || value.isEmpty()) return defaultValue;
//		
//		try
//		{
//			return Integer.parseInt(value);
//		} catch (Exception e)
//		{
//			return defaultValue;
//		}
//	}
//
//}
