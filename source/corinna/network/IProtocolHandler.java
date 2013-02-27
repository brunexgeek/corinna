package corinna.network;

import javax.bindlet.exception.BindletException;

import corinna.rpc.ProcedureCall;


public interface IProtocolHandler<R, P>
{

	/**
	 * Parse a protocol RPC request and create a procedure call object.
	 *  
	 * @param request
	 * @return
	 * @throws BindletException 
	 */
	public ProcedureCall readRequest( R request ) throws BindletException;
	
	/**
	 * Write a RPC response containing the given return value.
	 * 
	 * @param returnValue
	 * @return
	 */
	public void writeResponse( P response, Object returnValue ) throws BindletException;
	
}
