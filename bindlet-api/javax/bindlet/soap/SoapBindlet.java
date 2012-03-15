package javax.bindlet.soap;

import java.io.IOException;

import javax.bindlet.Bindlet;
import javax.bindlet.IComponentInformation;
import javax.bindlet.exception.BindletException;
import javax.bindlet.http.HttpStatus;


@SuppressWarnings("serial")
public abstract class SoapBindlet extends Bindlet<ISoapBindletRequest, ISoapBindletResponse>
{
	
	private static final String WSDL_SUFFIX = "?wsdl";
	
	private static final String COMPONENT_NAME = "SOAP Bindlet";

	private static final String COMPONENT_VERSION = "1.0";

	private static final String COMPONENT_IMPLEMENTOR = "Bruno Ribeiro";
	
	/*private static IComponentInformation COMPONENT_INFO = new ContextInfo(COMPONENT_NAME, 
		COMPONENT_VERSION, COMPONENT_IMPLEMENTOR);*/

	public SoapBindlet( ) throws BindletException
	{
		super();
	}
	
	protected void doWsdl( ISoapBindletRequest req, ISoapBindletResponse response ) throws BindletException, IOException
	{
	}
	
	protected void doPost( ISoapBindletRequest req, ISoapBindletResponse res )  throws BindletException,
		IOException
	{
	}

	protected boolean doAuthentication( ISoapBindletRequest request, ISoapBindletResponse response )
		throws BindletException, IOException
	{
		return true;
	}
	
	@Override
	public final void process( ISoapBindletRequest req, ISoapBindletResponse res )
		throws BindletException, IOException
	{
		if (req != null && req.getRequestURI().toString().endsWith(WSDL_SUFFIX))
			doWsdl(req, res);
		else
		if (req == null || req.getMessage() == null)
			res.sendError(HttpStatus.BAD_REQUEST);
		else
		{
			if (isRestricted() && !doAuthentication(req, res)) return;
			doPost(req, res);
		}
	}

	public abstract boolean isRestricted();
	
	/*@Override
	public IComponentInformation getBindletInfo()
	{
		return COMPONENT_INFO;
	}*/
	
}
