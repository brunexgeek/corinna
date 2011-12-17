package corinna.core.soap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.bindlet.IBindletContext;
import javax.bindlet.soap.ISoapBindletRequest;
import javax.bindlet.soap.ISoapBindletResponse;

import corinna.bindlet.http.HttpBindletRequest;
import corinna.bindlet.soap.SoapBindletContext;
import corinna.bindlet.soap.SoapBindletRequest;
import corinna.core.Context;
import corinna.core.IBindletRegistration;
import corinna.core.IService;


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
			String value = current.getBindletParameter(BINDLET_URL_MAPPING);
			if (value == null) continue;

			Pattern pattern = Pattern.compile(value);
			Matcher match = pattern.matcher( request.getResourcePath() );

			if (match.find() && match.start() == 0)
			{
				if (request instanceof HttpBindletRequest)
					((HttpBindletRequest)request).setBindletPath(match.group());
				return current;
			}
		}
		
		return null;
	}

	@Override
	protected boolean acceptRequest( ISoapBindletRequest request )
	{
		if (!(request instanceof SoapBindletRequest)) return false;
		
		SoapBindletRequest req = (SoapBindletRequest) request;
		
		// check if the URL of the request match with the current context path
		String value = getParameter(CONTEXT_URL_MAPPING);
		String path = req.getResourcePath();
		if (value == null || path == null) return false;

		//if (!value.isEmpty() && value.charAt( value.length()-1 ) != '/') value += '/';
		//if (!path.isEmpty() && path.charAt( path.length()-1 ) != '/') path += '/';
		if (value.isEmpty() || !value.startsWith("/")) value = '/' + value;
		//if (value.charAt( value.length()-1 ) != '/') value += '/';
		
		Pattern pattern = Pattern.compile(value);
		Matcher match = pattern.matcher(path);
		
		if (match.find() && match.start() == 0)
		{
			req.setContextPath(match.group());
			return true;
		}
		
		return false;
	}

}
