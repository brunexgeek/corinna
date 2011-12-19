package corinna.core.rest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.bindlet.IBindletContext;
import javax.bindlet.soap.ISoapBindletRequest;

import corinna.bindlet.http.HttpBindletRequest;
import corinna.bindlet.rest.IRestBindletRequest;
import corinna.bindlet.rest.IRestBindletResponse;
import corinna.bindlet.rest.RestBindletContext;
import corinna.bindlet.soap.SoapBindletContext;
import corinna.bindlet.soap.SoapBindletRequest;
import corinna.core.Context;
import corinna.core.IBindletRegistration;
import corinna.core.IService;


// TODO: esta classe é similar à 'corinna.core.http'
public class RestContext extends Context<IRestBindletRequest, IRestBindletResponse>
{

	private static final String BINDLET_URL_MAPPING = "urlMapping";
	
	private static final String CONTEXT_URL_MAPPING = "urlMapping";
	
	public RestContext( String name )
	{
		super(name);
	}

	@Override
	protected IBindletContext createBindletContext()
	{
		return new RestBindletContext(this);
	}

	@Override
	public IBindletRegistration getBindletRegistration( IRestBindletRequest request )
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
	protected boolean acceptRequest( IRestBindletRequest request )
	{
		if (!(request instanceof SoapBindletRequest)) return false;
		
		SoapBindletRequest req = (SoapBindletRequest) request;
		
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
