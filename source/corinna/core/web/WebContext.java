package corinna.core.web;


import javax.bindlet.http.IWebBindletRequest;
import javax.bindlet.http.IWebBindletResponse;

import corinna.bindlet.http.WebBindletRequest;
import corinna.core.Context;
import corinna.core.IBindletRegistration;
import corinna.core.http.HttpUtils;
import corinna.thread.ObjectLocker;


public abstract class WebContext<R extends IWebBindletRequest, P extends IWebBindletResponse>
	extends Context<R, P> implements IWebContext<R, P>
{

	private static final String BINDLET_URL_MAPPING = "urlMapping";

	private static final String CONTEXT_URL_MAPPING = "urlMapping";

	private ObjectLocker authenticatorLock;

	//private IHttpAuthenticator authenticator;

	public WebContext( String name )
	{
		super(name);
	//	authenticator = null;
		authenticatorLock = new ObjectLocker();
	}

	protected IBindletRegistration findRegistration( IWebBindletRequest request )
	{
		IBindletRegistration[] regs = getBindletRegistrations();
		String path = request.getResourcePath();

		for (IBindletRegistration current : regs)
		{
			String pattern = current.getBindletParameter(BINDLET_URL_MAPPING);

			if (pattern == null || path == null) continue;

			try
			{
				String value = HttpUtils.matchURI(pattern, path);
				if (value == null) continue;

				if (request instanceof WebBindletRequest)
					((WebBindletRequest) request).setBindletPath(value);
			} catch (Exception e)
			{
				continue;
			}

			return current;
		}

		return null;
	}

	protected boolean matchContextPath( IWebBindletRequest request )
	{
		// check if the URL of the request match with the current context path
		String pattern = getParameter(CONTEXT_URL_MAPPING);
		String path = request.getResourcePath();
		if (pattern == null || path == null) return false;

		try
		{
			String value = HttpUtils.matchURI(pattern, path);
			if (value == null) return false;

			if (request instanceof WebBindletRequest)
				((WebBindletRequest) request).setContextPath(value);
		} catch (Exception e)
		{
			return false;
		}

		return true;
	}

	/*@Override
	public void setAuthenticator( IHttpAuthenticator auth )
	{
		authenticatorLock.writeLock();
		try
		{
			authenticator = auth;
		} finally
		{
			authenticatorLock.writeUnlock();
		}
	}

	@Override
	public IHttpAuthenticator getAuthenticator()
	{
		IHttpAuthenticator auth;

		authenticatorLock.readLock();
		auth = authenticator;
		authenticatorLock.readUnlock();

		return auth;
	}*/

}
