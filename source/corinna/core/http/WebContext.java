package corinna.core.http;


import javax.bindlet.http.IWebBindletRequest;
import javax.bindlet.http.IWebBindletResponse;

import corinna.bindlet.http.WebBindletRequest;
import corinna.core.Context;
import corinna.core.IBindletRegistration;
import corinna.core.IContextConfig;
import corinna.core.IService;


//TODO: move to "corinna.core.http"
public abstract class WebContext<R extends IWebBindletRequest, P extends IWebBindletResponse>
	extends Context<R, P> implements IWebContext<R, P>
{

	private static final String BINDLET_URL_MAPPING = "urlMapping";

	private static final String CONTEXT_URL_MAPPING = "urlMapping";

	public WebContext( IContextConfig config, IService service )
	{
		super(config, service);
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
		String pattern = getConfig().getParameter(CONTEXT_URL_MAPPING, null);
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

}
