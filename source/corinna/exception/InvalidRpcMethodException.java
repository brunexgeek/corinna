/*
 * Copyright 2011-2012 Bruno Ribeiro
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

package corinna.exception;



/**
 * Disparada quando um metodo não é válido para uso através do mecanismo de RPC. Esta exceção é 
 * representada pelo valor inteiro 0x55.
 * 
 * @author Bruno Ribeiro &lt;brunoc@cpqd.com.br&gt;
 * @since 1.0
 * @see RpcValidator
 */
public class InvalidRpcMethodException extends RpcException 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4647887939387583391L;

	public InvalidRpcMethodException() 
	{
		super();
	}

	public InvalidRpcMethodException( String message, Throwable cause ) 
	{
		super(message, cause);
	}

	public InvalidRpcMethodException( Throwable cause ) 
	{
		super(cause);
	}

	public InvalidRpcMethodException( String message ) 
	{
		super(message);
	}

}
