package corinna.test;

import javax.bindlet.IBindletService;

import corinna.core.IServer;
import corinna.core.Service;
import corinna.exception.LifecycleException;
import corinna.network.INetworkConnector;


public class MyService extends Service
{

	public MyService( String name )
	{
		super(name);
	}

	@Override
	public IBindletService getBindletService()
	{
		// TODO Auto-generated method stub
		return null;
	}




}
