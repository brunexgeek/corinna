package corinna.core.http;


import javax.bindlet.IBindlet;
import javax.bindlet.IBindletContext;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;

import corinna.bindlet.http.HttpBindletContext;
import corinna.bindlet.http.HttpBindletRequest;
import corinna.core.Context;
import corinna.core.IBindletRegistration;


public class HttpContext extends Context<IHttpBindletRequest, IHttpBindletResponse> implements
	IHttpContext
{

	private static final String BINDLET_URL_MAPPING = "urlMapping";
	
	private static final String CONTEXT_URL_MAPPING = "urlMapping";

	public HttpContext( String name)
	{
		super(name);
	}

	@Override
	public IBindlet<IHttpBindletRequest, IHttpBindletResponse> createHttpBindlet(
		String bindletMapping )
	{
		return null;
	}

	@Override
	protected IBindletContext createBindletContext()
	{
		return new HttpBindletContext(this);
	}

	@Override
	public IBindletRegistration getBindletRegistration( IHttpBindletRequest request )
	{
		IBindletRegistration[] regs = getBindletRegistrations();

		for (IBindletRegistration current : regs)
		{
			String pattern = current.getBindletParameter(BINDLET_URL_MAPPING);
			String path = request.getResourcePath();
			if (pattern == null || path == null) continue;

			try
			{
				String value = HttpUtils.matchURI(pattern, path);
				if (value == null) continue;
				
				if (request instanceof HttpBindletRequest)
					((HttpBindletRequest)request).setBindletPath(value);
			} catch (Exception e)
			{
				continue;
			}

			return current;
		}
		
		return null;
	}

	@Override
	protected boolean acceptRequest( IHttpBindletRequest request )
	{
		// check if the URL of the request match with the current context path
		String pattern = getParameter(CONTEXT_URL_MAPPING);
		String path = request.getResourcePath();
		if (pattern == null || path == null) return false;

		try
		{
			String value = HttpUtils.matchURI(pattern, path);
			if (value == null) return false;
			
			if (request instanceof HttpBindletRequest)
				((HttpBindletRequest)request).setContextPath(value);
		} catch (Exception e)
		{
			return false;
		}
		
		return true;
	}

}
