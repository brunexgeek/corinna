package corinna.test;


import corinna.service.rpc.MultipleReturnValue;
import corinna.service.rpc.PublicProcedure;
import corinna.service.rpc.ServiceImplementation;


@ServiceImplementation(isStateless=true)
public class MyImpl implements MyInterface
{

	public MyImpl( Object obj )
	{
		
	}
	
	@Override
	public int getCount( Float id, Boolean sector )
	{
		if (id != null)
			System.out.println("ID: " + id);
		System.out.println("Sector: " + sector);
		if (id != null)
			return id.intValue();
		else
			return -1;
	}

}
