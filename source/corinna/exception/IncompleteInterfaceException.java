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
package corinna.exception;

public class IncompleteInterfaceException extends RpcException 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2976464056458408432L;

	public IncompleteInterfaceException() 
	{
		super();
	}

	public IncompleteInterfaceException( String message, Throwable cause ) 
	{
		super(message, cause);
	}

	public IncompleteInterfaceException( Throwable cause ) 
	{
		super(cause);
	}

	public IncompleteInterfaceException( String message ) 
	{
		super(message);
	}

}
