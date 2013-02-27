package corinna.http.core.auth;


import javax.bindlet.http.IWebBindletRequest;


public interface IHttpAuthenticator
{

	public boolean authenticate( IWebBindletRequest request );
	
	public IUserDatabase getDatabase();

}
