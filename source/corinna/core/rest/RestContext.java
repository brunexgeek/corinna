package corinna.core.rest;

import javax.bindlet.IBindletContext;

import corinna.bindlet.rest.IRestBindletRequest;
import corinna.bindlet.rest.IRestBindletResponse;
import corinna.bindlet.rest.RestBindletContext;
import corinna.core.IBindletRegistration;
import corinna.core.IService;
import corinna.core.http.HttpContextInfo;
import corinna.core.http.WebContext;
import corinna.util.IComponentInformation;
import corinna.util.conf.ISection;


public class RestContext extends WebContext<IRestBindletRequest, IRestBindletResponse>
{

	private RestBindletContext bindletContext = null;
	
	public RestContext( String name, IService service, ISection config )
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
