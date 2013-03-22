package corinna.http.core.auth;


import javax.bindlet.http.IHttpBindletRequest;


public interface IHttpAuthenticator
{

	public boolean authenticate( IHttpBindletRequest request );
	
	public IUserDatabase getDatabase();

}
