package corinna.core.soap;

import javax.bindlet.IBindletContext;
import javax.bindlet.soap.ISoapBindletRequest;
import javax.bindlet.soap.ISoapBindletResponse;

import corinna.bindlet.soap.SoapBindletContext;
import corinna.core.IBindletRegistration;
import corinna.core.IContextConfig;
import corinna.core.IService;
import corinna.core.http.WebContext;
import corinna.util.IComponentInformation;
import corinna.util.conf.ISection;


public class SoapContext extends WebContext<ISoapBindletRequest,ISoapBindletResponse>
{
	
	private SoapBindletContext bindletContext = null;
	
	public SoapContext( IContextConfig config, IService service )
	{
		super(config, service);
	}

	@Override
	protected IBindletContext createBindletContext()
	{
		if (bindletContext == null)
			bindletContext = new SoapBindletContext(this);
		return bindletContext;
	}

	@Override
	public IComponentInformation getContextInfo()
	{
		return SoapContextInfo.getInstance();
	}

	@Override
	public Class<?> getRequestType()
	{
		return ISoapBindletRequest.class;
	}
	
	@Override
	public Class<?> getResponseType()
	{
		return ISoapBindletResponse.class;
	}

	@Override
	public IBindletRegistration getBindletRegistration( ISoapBindletRequest request )
	{
		return findRegistration(request);
	}

	@Override
	protected boolean acceptRequest( ISoapBindletRequest request )
	{
		return matchContextPath(request);
	}
	
}
