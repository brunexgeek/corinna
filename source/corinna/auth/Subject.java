package corinna.auth;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;




@SuppressWarnings("serial")
public class Subject implements ISubject
{

	private boolean isAuthenticated;
	
	private List<ICredential> credentials;
	
	private List<Principal> principals;
	
	public Subject( )
	{
		isAuthenticated = false;
		credentials = new ArrayList<ICredential>();
		principals = new ArrayList<Principal>();
	}
	
	@Override
	public Principal[] getPrincipals()
	{
		return principals.toArray( new Principal[0] );
	}

	@Override
	public boolean isAuthenticated()
	{
		return isAuthenticated;
	}

	@Override
	public ICredential[] getCredentials()
	{
		return credentials.toArray( new ICredential[0] );
	}

	@Override
	public void update() throws LoginException
	{
		
	}
	


}
