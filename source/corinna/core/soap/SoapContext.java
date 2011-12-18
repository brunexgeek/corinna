package corinna.core.soap;

import javax.bindlet.IBindletContext;
import javax.bindlet.soap.ISoapBindletRequest;
import javax.bindlet.soap.ISoapBindletResponse;

import corinna.bindlet.soap.SoapBindletContext;
import corinna.bindlet.soap.SoapBindletRequest;
import corinna.core.Context;
import corinna.core.IBindletRegistration;
import corinna.core.IService;
import corinna.core.http.HttpUtils;


public class SoapContext extends Context<ISoapBindletRequest, ISoapBindletResponse> implements ISoapContext
{

	private static final String BINDLET_URL_MAPPING = "urlMapping";
	
	private static final String CONTEXT_URL_MAPPING = "urlMapping";

	
	public SoapContext( String name, IService service )
	{
		super(name, service);
	}

	@Override
	protected IBindletContext createBindletContext()
	{
		return new SoapBindletContext(this);
	}

	@Override
	public IBindletRegistration getBindletRegistration( ISoapBindletRequest request )
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
				
				if (request instanceof SoapBindletRequest)
					((SoapBindletRequest)request).setBindletPath(value);
			} catch (Exception e)
			{
				continue;
			}

			return current;
		}
		
		return null;
	}

	@Override
	protected boolean acceptRequest( ISoapBindletRequest request )
	{
		// check if the URL of the request match with the current context path
		String pattern = getParameter(CONTEXT_URL_MAPPING);
		String path = request.getResourcePath();
		if (pattern == null || path == null) return false;

		try
		{
			String value = HttpUtils.matchURI(pattern, path);
			if (value == null) return false;
			
			if (request instanceof SoapBindletRequest)
				((SoapBindletRequest)request).setContextPath(value);
		} catch (Exception e)
		{
			return false;
		}
		
		return true;
	}

}
