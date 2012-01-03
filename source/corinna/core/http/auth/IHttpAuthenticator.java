package corinna.core.http.auth;


import javax.bindlet.http.IWebBindletRequest;


public interface IHttpAuthenticator
{

	public boolean authenticate( IWebBindletRequest request );
	
	public IUserDatabase getDatabase();

}
