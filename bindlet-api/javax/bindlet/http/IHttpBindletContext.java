package javax.bindlet.http;


import java.security.NoSuchAlgorithmException;

import javax.bindlet.IBindletContext;


public interface IHttpBindletContext extends IBindletContext
{

	/**
	 * Create a new authentication token based on the specified request.
	 * 
	 * @param request
	 * @return
	 */
	//public IAuthToken createAuthToken( IHttpBindletRequest request )
	//	throws NoSuchAlgorithmException;

	/**
	 * Create a new authentication token parsing the given input data. The input data must obay the
	 * RFC 2617 format (can be a "WWW-Authenticate Response Header" or
	 * "Authorization Request Header"). If the input data is invalid or the given authentication
	 * method is not supported, this method returns <code>null</code>.
	 * 
	 * @param data
	 * @return
	 */
	//public IAuthToken createAuthToken( String data );

}
