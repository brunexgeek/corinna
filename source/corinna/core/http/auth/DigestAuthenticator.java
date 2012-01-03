package corinna.core.http.auth;


import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.bindlet.http.IWebBindletRequest;

import org.jboss.netty.handler.codec.http.HttpHeaders;

import corinna.thread.ObjectLocker;


// TODO: create a array based pool of 'NonceInfo'
// TODO: make this class thread-safe
public class DigestAuthenticator implements IHttpAuthenticator
{

	private static final Charset CHARSET = Charset.forName("ISO-8859-1");
	
	private Map<String, NonceInfo> nonces;

	private ObjectLocker noncesLock;

	private static int counter = 0;

	private IUserDatabase database;

	public DigestAuthenticator( IUserDatabase database )
	{
		if (database == null)
			throw new NullPointerException("The user database object can not be null");

		this.database = database;

		nonces = new HashMap<String, NonceInfo>();
		noncesLock = new ObjectLocker();
	}

	public AuthenticateResponse createAuthenticateResponse( String realm )
	{
		String nonce;

		while (true)
		{
			nonce = generateNonce(realm);
			if (!nonces.containsKey(nonce)) break;
		}

		AuthenticateResponse resp = new AuthenticateResponse(realm, nonce);
		NonceInfo info = new NonceInfo(resp, NonceInfo.DEFAULT_NONCE_LIFETIME);

		noncesLock.writeLock();
		nonces.put(nonce, info);
		noncesLock.writeUnlock();

		return resp;
	}

	public boolean containsNonce( String nonce )
	{
		NonceInfo info = nonces.get(nonce);
		if (info != null)
		{
			if (info.isExpired())
			{
				nonces.remove(nonce);
				return false;
			}
			return true;
		}
		return false;
	}

	private String generateNonce( String realm )
	{
		// TODO: create a array pool of 'MessageDigest'?
		try
		{
			MessageDigest digest = new MessageDigest("MD5");
			digest.update(realm, CHARSET);
			digest.update( System.currentTimeMillis() );
			return digest.getHashString();
		} catch (Exception e)
		{
			return null;
		}
	}

	@Override
	public boolean authenticate( IWebBindletRequest request )
	{
		String value = request.getHeader(HttpHeaders.Names.AUTHORIZATION);
		if (value != null)
		{
			try
			{
				AuthorizationRequest auth = new AuthorizationRequest(value);
				return authenticate(auth);
			} catch (Exception e)
			{
				return false;
			}
		}
		return false;
	}

	public boolean authenticate( AuthorizationRequest request )
	{
		if (request == null) return false;
		
		try
		{
			// get the server nonce information
			NonceInfo info = getNonceInfo( request.getNonce() );
			if (info == null || info.isExpired()) return false;
			
			// retrieve the user information from database
			IUser user = database.getUser(request.getUserName());
			if (user == null) return false;
			
			MessageDigest digest = new MessageDigest("MD5");
			
			// Note: for digest authentication, the password is the 'A1' value instead the plain
			//       password
			String hashA1 = user.getPassword();
			/*StringBuffer sb = new StringBuffer();
			sb.append(request.getUserName());
			sb.append(":");
			sb.append(request.getRealm());
			sb.append(":michelle");
			digest.update(sb.toString(), CHARSET);
			String hashA1 = digest.getHashString();*/

			// calculate the hash for A2
			digest.update("GET:" + request.getUri(), CHARSET);
			String hashA2 = digest.getHashString();

			// TODO: implement the 'nonce-counter' in server-side instead use the client value
			
			// calculate the hash for the response
			StringBuffer sb = new StringBuffer();
			sb.append(hashA1);
			sb.append(":");
			sb.append(request.getNonce());
			sb.append(":");
			sb.append(request.getNonceCount());
			sb.append(":");
			sb.append(request.getCNonce());
			sb.append(":");
			sb.append(request.getQopOptions());
			sb.append(":");
			sb.append(hashA2);
			digest.update(sb.toString() , CHARSET);
			String response = digest.getHashString();
			
			return (response.equalsIgnoreCase(request.getResponse()));
		} catch (Exception e)
		{
			return false;
		}
	}

	public NonceInfo getNonceInfo( String nonce )
	{
		noncesLock.readLock();
		NonceInfo info = nonces.get(nonce);
		noncesLock.readUnlock();
		
		return info;
	}
	
	public class NonceInfo
	{

		/**
		 * Default nonce life time, in seconds (900 seconds).
		 */
		private static final int DEFAULT_NONCE_LIFETIME = 900;

		private Long expireTime;

		private AuthenticateResponse resp;

		public NonceInfo( AuthenticateResponse resp, long lifeTime )
		{
			if (resp == null)
				throw new NullPointerException("The authenticate response object can not be null");

			this.resp = resp;
			// set the initial lifetime
			if (lifeTime <= 0) lifeTime = DEFAULT_NONCE_LIFETIME;
			expireTime = System.currentTimeMillis() + (lifeTime * 1000);
		}

		public boolean isExpired()
		{
			synchronized (expireTime)
			{
				return (System.currentTimeMillis() > expireTime);
			}
		}

		public AuthenticateResponse getAuthResponse()
		{
			return resp;
		}

	}

	@Override
	public IUserDatabase getDatabase()
	{
		return database;
	}

}
