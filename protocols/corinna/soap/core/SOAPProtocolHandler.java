package corinna.soap.core;

import javax.bindlet.exception.BindletException;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;

import corinna.network.IProtocolHandler;
import corinna.rpc.ProcedureCall;


public class SOAPProtocolHandler implements IProtocolHandler<IHttpBindletRequest, IHttpBindletResponse>
{

	@Override
	public ProcedureCall readRequest( IHttpBindletRequest request ) throws BindletException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeResponse( IHttpBindletResponse response, Object returnValue )
		throws BindletException
	{
		// TODO Auto-generated method stub
		
	}



}
