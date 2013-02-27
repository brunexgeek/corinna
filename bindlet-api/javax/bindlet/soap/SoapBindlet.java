package javax.bindlet.soap;


import java.io.IOException;

import javax.bindlet.Bindlet;
import javax.bindlet.IBindletAuthenticator;
import javax.bindlet.exception.BindletException;
import javax.bindlet.http.HttpMethod;
import javax.bindlet.http.HttpStatus;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;
import javax.bindlet.rpc.IProcedureCall;


@SuppressWarnings("serial")
public abstract class SoapBindlet extends Bindlet<IHttpBindletRequest, IHttpBindletResponse>
{

	private static final String WSDL_SUFFIX = "?wsdl";

	private static final String INIT_PARAM_IS_RESTRICTED = "isRestricted";

	private IBindletAuthenticator authenticator = null;

	public SoapBindlet() throws BindletException
	{
		super();
	}

	protected void doGet( IHttpBindletRequest request, IHttpBindletResponse response )
		throws BindletException, IOException
	{
	}

	protected abstract Object doCall( IProcedureCall request ) throws BindletException;

	protected void doPost( IHttpBindletRequest request, IHttpBindletResponse response )
		throws BindletException, IOException
	{
	}

	protected boolean doAuthentication( IHttpBindletRequest request, IHttpBindletResponse response )
		throws BindletException, IOException
	{
		if (authenticator  != null)
			return authenticator.authenticate(request, response);
		else
			throw new BindletException("No authenticator configured");
	}

	@Override
	public final void process( IHttpBindletRequest request, IHttpBindletResponse response )
		throws BindletException, IOException
	{
		response.setContentType("text/xml");
		if (request == null) response.sendError(HttpStatus.BAD_REQUEST);

		if (request.getHttpMethod() == HttpMethod.GET)
		{
			doGet(request, response);
		}
		else
		if (request.getHttpMethod() == HttpMethod.POST)
		{
			if (isRestricted() && !doAuthentication(request, response)) return;
			doPost(request, response);
		}
		else
		{
			response.setContentType("text/html");
			response.sendError(HttpStatus.METHOD_NOT_ALLOWED);
		}
	}

	protected abstract IProcedureCall getProcedureCall( IHttpBindletRequest request,
		IHttpBindletResponse response ) throws BindletException;

	public boolean isRestricted()
	{
		String value = getInitParameter(INIT_PARAM_IS_RESTRICTED);
		return (authenticator != null && (value != null && value.equalsIgnoreCase("true")));
	}

	protected void setAuthenticator( IBindletAuthenticator authenticator )
	{
		this.authenticator = authenticator;
	}
	
	protected IBindletAuthenticator getAuthenticator()
	{
		return authenticator;
	}
	
}
