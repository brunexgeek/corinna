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
import corinna.util.IComponentInformation;


public class SoapContext extends Context<ISoapBindletRequest, ISoapBindletResponse> implements ISoapContext
{

	private static final String BINDLET_URL_MAPPING = "urlMapping";
	
	private static final String CONTEXT_URL_MAPPING = "urlMapping";

	
	public SoapContext( String name )
	{
		super(name);
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

	@Override
	public IComponentInformation getContextInfo()
	{
		return new SoapContextInfo();
	}
	
	public class SoapContextInfo  implements IComponentInformation
	{

		@Override
		public String getComponentName()
		{
			return "SOAP Context";
		}

		@Override
		public String getComponentVersion()
		{
			return "1.0";
		}

		@Override
		public String getComponentImplementor()
		{
			return "Bruno Ribeiro";
		}
		
	}

}
