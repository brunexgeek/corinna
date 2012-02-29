package corinna.rest.core;

import javax.bindlet.IBindletContext;

import corinna.core.IBindletRegistration;
import corinna.core.IContextConfig;
import corinna.core.IService;
import corinna.http.core.HttpContextInfo;
import corinna.http.core.WebContext;
import corinna.rest.bindlet.IRestBindletRequest;
import corinna.rest.bindlet.IRestBindletResponse;
import corinna.rest.bindlet.RestBindletContext;
import corinna.util.IComponentInformation;


public class RestContext extends WebContext<IRestBindletRequest, IRestBindletResponse>
{

	private RestBindletContext bindletContext = null;
	
	public RestContext( IContextConfig config, IService service )
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
			bindletContext = new RestBindletContext(this);
		return bindletContext;
	}
	
	@Override
	public Class<?> getRequestType()
	{
		return IRestBindletRequest.class;
	}
	
	@Override
	public Class<?> getResponseType()
	{
		return IRestBindletResponse.class;
	}

	@Override
	public IBindletRegistration getBindletRegistration( IRestBindletRequest request )
	{
		return findRegistration(request);
	}

	@Override
	protected boolean acceptRequest( IRestBindletRequest request )
	{
		return matchContextPath(request);
	}
}
