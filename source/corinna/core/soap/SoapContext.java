package corinna.core.soap;

import javax.bindlet.IBindletContext;
import javax.bindlet.soap.ISoapBindletRequest;
import javax.bindlet.soap.ISoapBindletResponse;

import corinna.bindlet.soap.SoapBindletContext;
import corinna.core.IBindletRegistration;
import corinna.core.web.WebContext;
import corinna.util.IComponentInformation;


public class SoapContext extends WebContext<ISoapBindletRequest,ISoapBindletResponse>
{
	
	private SoapBindletContext bindletContext = null;
	
	public SoapContext( String name )
	{
		super(name);
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
