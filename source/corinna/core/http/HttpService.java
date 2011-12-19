package corinna.core.http;

import javax.bindlet.IBindletService;

import corinna.core.IServer;
import corinna.core.Service;


public abstract class HttpService extends Service
{

	public HttpService( String name )
	{
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public IBindletService getBindletService()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
