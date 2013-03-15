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


public class ComponentException extends GenericException
{

	private static final long serialVersionUID = -8872895865742413605L;

	public ComponentException()
	{
		super();
	}

	public ComponentException( String message )
	{
		super(message);
	}

	public ComponentException( String message, Throwable cause )
	{
		super(message, cause);
	}

	public ComponentException( Throwable cause )
	{
		super(cause);
	}
	
}
