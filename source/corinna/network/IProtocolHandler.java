package corinna.network;

import corinna.rpc.ProcedureCall;


public interface IProtocolHandler<R, P>
{

	/**
	 * Parse a protocol RPC request and create a procedure call object.
	 *  
	 * @param request
	 * @return
	 */
	public ProcedureCall readRequest( R request );
	
	/**
	 * Write a RPC response containing the given return value.
	 * 
	 * @param returnValue
	 * @return
	 */
	public P writeResponse( Object returnValue );
	
}
