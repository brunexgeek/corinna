package corinna.auth;

import javax.security.auth.login.LoginException;


public interface ICredential
{

	public void update() throws LoginException;
	
}
