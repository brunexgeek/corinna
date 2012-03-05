package corinna.auth.login;

import javax.security.auth.login.LoginException;

import corinna.auth.ISubject;


public interface ILoginContext
{

	public ISubject getSubject();

	public void logout() throws LoginException;

	public void login() throws LoginException;
	
	public void addListener( ILoginContextListener listener );
	
	public void removeListener( ILoginContextListener listener );
	
	public void removeListener( String listenerName );
	
}
