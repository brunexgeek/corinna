package corinna.network;

import java.net.InetSocketAddress;

import corinna.core.IBasicConfig;


public interface IConnectorConfig extends IBasicConfig
{

	public String getConnectorName();
	
	public InetSocketAddress getAddress();
	
	public int getMaxWorkers();
	
	public int getPort();
	
	public String getHostName();
	
}
