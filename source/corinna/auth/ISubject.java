package corinna.auth;

import java.io.Serializable;
import java.security.Principal;

import javax.security.auth.login.LoginException;


public interface ISubject extends Serializable
{
	
	/**
	 * Returns a array containing all associated principals.
	 * 
	 * @return
	 */
	public Principal[] getPrincipals();
	
	/**
	 * Return <code>true</code> if the subject has, at least, one active credential.
	 * 
	 * @return
	 */
	public boolean isAuthenticated();
	
	public ICredential getCredential( String name );
	
	public ICredential[] getCredentials();
	
	/**
	 * Try to update all subject credentials.
	 * 
	 * @throws LoginException
	 */
	public void update() throws LoginException;
	
}
