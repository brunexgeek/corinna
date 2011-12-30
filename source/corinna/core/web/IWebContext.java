package corinna.core.web;


import java.security.NoSuchAlgorithmException;

import javax.bindlet.http.IAuthToken;
import javax.bindlet.http.IWebBindletRequest;
import javax.bindlet.http.IWebBindletResponse;

import corinna.core.IContext;
import corinna.core.http.IHttpAuthenticator;


public interface IWebContext<R extends IWebBindletRequest, P extends IWebBindletResponse> extends
	IContext<R, P>
{

	public void setAuthenticator( IHttpAuthenticator listener );

	public IHttpAuthenticator getAuthenticator();

	public IAuthToken createAuthToken( R request ) throws NoSuchAlgorithmException;

}
