package corinna.bindlet.soap;


import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;
import javax.bindlet.soap.ISoapBindletRequest;
import javax.bindlet.soap.ISoapBindletResponse;

import corinna.bindlet.BindletContext;
import corinna.core.soap.ISoapContext;
import corinna.network.IProtocol;
import corinna.util.IComponentInformation;

public class SoapBindletContext extends BindletContext<ISoapBindletRequest,ISoapBindletResponse>
{

	public SoapBindletContext( ISoapContext context )
	{
		super(context);
	}

	@Override
	public IComponentInformation getContextInfo()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> getContextRequestType()
	{
		return IHttpBindletRequest.class;
	}

	@Override
	public Class<?> getContextResponseType()
	{
		return IHttpBindletResponse.class;
	}


}
