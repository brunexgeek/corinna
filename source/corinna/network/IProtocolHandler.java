package corinna.network;


import java.io.IOException;

import javax.bindlet.exception.BindletException;
import javax.bindlet.rpc.IProcedureCall;

import corinna.rpc.ProcedureCall;


/**
 * Utility class responsible to perform translations between protocol specific messages and objects
 * of the RPC mechanism.
 * 
 * @author Bruno Ribeiro
 * 
 * @param <R>
 *            Request class type.
 * @param <P>
 *            Response class type.
 */
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
	public void writeResponse( P response, IProcedureCall procedure, Object returnValue )
		throws BindletException, IOException;

	/**
	 * Write a RPC response containing the given exception.
	 * 
	 * @param returnValue
	 * @return
	 */
	public void writeException( P response, Exception exception ) throws BindletException,
		IOException;

	public IProtocol getProtocol();
	
}
