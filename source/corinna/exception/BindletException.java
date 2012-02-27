/*
 * Copyright 2011 Bruno Ribeiro <brunei@users.sourceforge.net>
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

public class BindletException extends GenericException
{

	private static final long serialVersionUID = 933290522610959630L;

	public BindletException()
	{
		super();
	}

	public BindletException( String message )
	{
		super(message);
	}

	public BindletException( String message, Throwable rootCause )
	{
		super(message, rootCause);
	}

	public BindletException( Throwable rootCause )
	{
		super(rootCause);
	}

}
