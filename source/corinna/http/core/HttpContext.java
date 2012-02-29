package corinna.http.core;


import javax.bindlet.IBindletContext;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;

import corinna.core.IBindletRegistration;
import corinna.core.IContextConfig;
import corinna.core.IService;
import corinna.http.bindlet.HttpBindletContext;
import corinna.util.IComponentInformation;


public class HttpContext extends WebContext<IHttpBindletRequest,IHttpBindletResponse>
{
	
	private HttpBindletContext bindletContext = null;
	
	public HttpContext( IContextConfig config, IService service )
	{
		super(config, service);
	}

	@Override
	public IComponentInformation getContextInfo()
	{
		return HttpContextInfo.getInstance();
	}


	@Override
	protected IBindletContext createBindletContext()
	{
		if (bindletContext == null)
			bindletContext = new HttpBindletContext(this);
		return bindletContext;
	}
	
	@Override
	public Class<?> getRequestType()
	{
		return IHttpBindletRequest.class;
	}
	
	@Override
	public Class<?> getResponseType()
	{
		return IHttpBindletResponse.class;
	}

	@Override
	public IBindletRegistration getBindletRegistration( IHttpBindletRequest request )
	{
		return findRegistration(request);
	}

	@Override
	protected boolean acceptRequest( IHttpBindletRequest request )
	{
		return matchContextPath(request);
	}
	
}
