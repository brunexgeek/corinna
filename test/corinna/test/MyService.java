package corinna.test;

import javax.bindlet.IBindletService;

import corinna.core.IServer;
import corinna.core.Service;
import corinna.exception.LifecycleException;
import corinna.network.INetworkConnector;


public class MyService extends Service
{

	public MyService( String name, IServer server )
	{
		super(name, server);
	}

	@Override
	public IBindletService getBindletService()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub
		
	}


}
