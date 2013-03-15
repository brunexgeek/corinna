/*
 * Copyright 2011-2013 Bruno Ribeiro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
