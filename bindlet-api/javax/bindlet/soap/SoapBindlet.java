package javax.bindlet.soap;


import java.io.IOException;

import javax.bindlet.Bindlet;
import javax.bindlet.exception.BindletException;
import javax.bindlet.http.HttpStatus;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;

import corinna.rpc.IProcedureCall;
import corinna.rpc.ProcedureCall;


@SuppressWarnings("serial")
public abstract class SoapBindlet extends Bindlet<IHttpBindletRequest, IHttpBindletResponse>
{

	private static final String WSDL_SUFFIX = "?wsdl";

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
		return !isRestricted();
	}

	@Override
	public final void process( IHttpBindletRequest request, IHttpBindletResponse response )
		throws BindletException, IOException
	{
		if (request == null)
			response.sendError(HttpStatus.BAD_REQUEST);

		if (request.getHttpMethod().equalsIgnoreCase("GET"))
		{
			if (request != null && request.getRequestURI().toString().endsWith(WSDL_SUFFIX))
				doGet(request, response);
			else
				response.sendError(HttpStatus.BAD_REQUEST);
		}
		else
		{
			if (isRestricted() && !doAuthentication(request, response)) return;
			doPost(request, response);
		}	
	}
	
	protected abstract ProcedureCall getProcedureCall( IHttpBindletRequest request, IHttpBindletResponse response );
	
	public abstract boolean isRestricted();

}
