package corinna.core.http;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.bindlet.IBindlet;
import javax.bindlet.IBindletContext;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;

import corinna.bindlet.http.HttpBindletContext;
import corinna.bindlet.http.HttpBindletRequest;
import corinna.core.Context;
import corinna.core.IBindletRegistration;
import corinna.core.IService;

// TODO: essa classe é igual à 'SoapContext'
public class HttpContext extends Context<IHttpBindletRequest, IHttpBindletResponse> implements
	IHttpContext
{

	private static final String BINDLET_URL_MAPPING = "urlMapping";
	
	private static final String CONTEXT_URL_MAPPING = "urlMapping";

	public HttpContext( String name, IService service )
	{
		super(name, service);
	}

	@Override
	public IBindlet<IHttpBindletRequest, IHttpBindletResponse> createHttpBindlet(
		String bindletMapping )
	{
		return null;
	}

	@Override
	protected IBindletContext createBindletContext()
	{
		return new HttpBindletContext(this);
	}

	@Override
	public IBindletRegistration getBindletRegistration( IHttpBindletRequest request )
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
	protected boolean acceptRequest( IHttpBindletRequest request )
	{
		if (!(request instanceof HttpBindletRequest)) return false;
		
		HttpBindletRequest req = (HttpBindletRequest) request;
		
		// check if the URL of the request match with the current context path
		String value = getParameter(CONTEXT_URL_MAPPING);
		String path = req.getResourcePath();
		if (value == null || path == null) return false;

		if (!value.isEmpty() && value.charAt( value.length()-1 ) != '/') value += '/';
		if (!path.isEmpty() && path.charAt( path.length()-1 ) != '/') path += '/';

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
