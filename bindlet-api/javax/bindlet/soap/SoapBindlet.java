package javax.bindlet.soap;

import java.io.IOException;

import javax.bindlet.Bindlet;
import javax.bindlet.http.HttpStatus;

import corinna.core.ContextInfo;
import corinna.exception.BindletException;
import corinna.util.IComponentInformation;


@SuppressWarnings("serial")
public abstract class SoapBindlet extends Bindlet<ISoapBindletRequest, ISoapBindletResponse>
{
	
	private static final String WSDL_SUFFIX = "?wsdl";
	
	private static final String COMPONENT_NAME = "SOAP Bindlet";

	private static final String COMPONENT_VERSION = "1.0";

	private static final String COMPONENT_IMPLEMENTOR = "Bruno Ribeiro";
	
	private static IComponentInformation COMPONENT_INFO = new ContextInfo(COMPONENT_NAME, 
		COMPONENT_VERSION, COMPONENT_IMPLEMENTOR);

	public SoapBindlet( ) throws BindletException
	{
		super();
	}
	
	public void doWsdl( ISoapBindletRequest req, ISoapBindletResponse response ) throws BindletException, IOException
	{
	}
	
	public void doPost( ISoapBindletRequest req, ISoapBindletResponse res )  throws BindletException,
		IOException
	{
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
			doPost(req, res);
	}

	@Override
	public IComponentInformation getBindletInfo()
	{
		return COMPONENT_INFO;
	}
	
}
