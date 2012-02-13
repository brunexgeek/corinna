package corinna.core.http;


import javax.bindlet.IBindletContext;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;

import corinna.bindlet.http.HttpBindletContext;
import corinna.core.IBindletRegistration;
import corinna.core.IService;
import corinna.util.IComponentInformation;
import corinna.util.conf.ISection;


public class HttpContext extends WebContext<IHttpBindletRequest,IHttpBindletResponse>
{
	
	private HttpBindletContext bindletContext = null;
	
	public HttpContext( String name, IService service, ISection config )
	{
		super(name, service, config);
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
