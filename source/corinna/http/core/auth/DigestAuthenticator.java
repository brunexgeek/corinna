package corinna.http.core.auth;


import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.bindlet.http.HttpMethod;
import javax.bindlet.http.IWebBindletRequest;

import org.jboss.netty.handler.codec.http.HttpHeaders;

import corinna.thread.ObjectLocker;


// TODO: create a array based pool of 'NonceInfo'
public class DigestAuthenticator implements IHttpAuthenticator
{

	private static final Charset CHARSET = Charset.forName("ISO-8859-1");
	
	private Map<String, NonceInfo> nonces;

	private ObjectLocker noncesLock;

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
		return createAuthenticateResponse(realm, NonceInfo.DEFAULT_NONCE_LIFETIME);
	}
	
	public AuthenticateResponse createAuthenticateResponse( String realm, long lifeTime )
	{
		String nonce;

		while (true)
		{
			nonce = generateNonce(realm);
			if (!nonces.containsKey(nonce)) break;
		}

		// TODO: we need avoid create new instances here for each request (create a pool?)
		AuthenticateResponse resp = new AuthenticateResponse(realm, nonce);
		NonceInfo info = new NonceInfo(resp, lifeTime);

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

	public void releaseNonce( String nonce )
	{
		noncesLock.writeLock();
		NonceInfo info = nonces.remove(nonce);
		noncesLock.writeUnlock();
		
		info.invalidate();
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
				boolean result = authenticate(request.getHttpMethod(), auth);
				if (result) request.setUserName(auth.getUserName());
			} catch (Exception e)
			{
				return false;
			}
		}
		return false;
	}

	public boolean authenticate( HttpMethod method, AuthorizationRequest request )
	{
		if (request == null) return false;
		String nonce = request.getNonce();

		try
		{
			// get the server nonce information
			NonceInfo info = getNonceInfo(nonce);
			if (info == null) return false;
			info.incCount();
			// check if the current nonce expired or have an inconsistent count
			if (info.isExpired() || !info.checkCount(request.getNonceCount()))
			{
				releaseNonce(nonce);
				return false;
			}
			
			// retrieve the user information from database
			IUser user = database.getUser(request.getUserName());
			if (user == null) 
			{
				releaseNonce(nonce);
				return false;
			}
			
			MessageDigest digest = new MessageDigest("MD5");
			
			// Note: for digest authentication, the password is the 'HA1' value instead the plain
			//       password
			String hashA1 = user.getPassword();

			// calculate the hash for 'A2'
			StringBuffer sb = new StringBuffer();
			sb.append(method);
			sb.append(":");
			sb.append(request.getDigestUri());
			digest.update(sb.toString(), CHARSET);
			String hashA2 = digest.getHashString();

			// TODO: implement the 'nonce-counter' in server-side instead use the client value
			
			// calculate the hash for the response
			sb.delete(0, sb.length());
			sb.append(hashA1);
			sb.append(":");
			sb.append(request.getNonce());
			sb.append(":");
			sb.append(request.getNonceCount());
			sb.append(":");
			sb.append(request.getClientNonce());
			sb.append(":");
			sb.append(request.getQopOptions());
			sb.append(":");
			sb.append(hashA2);
			digest.update(sb.toString() , CHARSET);
			String response = digest.getHashString();
			
			boolean result = (response.equalsIgnoreCase(request.getResponse()));
			if (!result) releaseNonce(nonce);
			return result;
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

		private Long expireTime = 0L;
		
		private Long count = 0L;

		private AuthenticateResponse resp;
		
		private long lifeTime = DEFAULT_NONCE_LIFETIME;

		public NonceInfo( AuthenticateResponse resp, long lifeTime )
		{
			if (resp == null)
				throw new NullPointerException("The authenticate response object can not be null");

			this.resp = resp;
			update(lifeTime);
		}

		/**
		 * Check if the server nonce counter value match with the specified value.
		 * 
		 * @param nonceCount Nonce count as a <code>String</code>
		 * @return
		 */
		public boolean checkCount( String nonceCount )
		{
			String value = String.format("%08x", getCount());
			return value.equalsIgnoreCase(nonceCount);
		}

		public boolean isExpired()
		{
			synchronized (expireTime)
			{
				return (expireTime > 0 && System.currentTimeMillis() > expireTime);
			}
		}

		public AuthenticateResponse getAuthResponse()
		{
			return resp;
		}
		
		public long getCount()
		{
			synchronized (count)
			{
				return count;
			}
		}
		
		public long incCount()
		{
			synchronized (count)
			{
				return ++count;
			}
		}
		
		public void update(  )
		{
			update(lifeTime);
		}
		
		public void update( long lifeTime )
		{
			if (lifeTime <= 0) lifeTime = DEFAULT_NONCE_LIFETIME;
			
			synchronized (expireTime)
			{
				expireTime = System.currentTimeMillis() + (lifeTime * 1000);
			}
		}
		
		public void invalidate()
		{
			synchronized (expireTime)
			{
				expireTime = -1L;
			}
		}
		
	}

	@Override
	public IUserDatabase getDatabase()
	{
		return database;
	}

}
