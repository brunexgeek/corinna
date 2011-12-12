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


import org.jboss.netty.handler.codec.http.HttpResponseStatus;


public class WebServicesException extends Exception
{

	private static final long serialVersionUID = -5536530781467265569L;

	private HttpResponseStatus status = HttpResponseStatus.INTERNAL_SERVER_ERROR;;

	public WebServicesException( HttpResponseStatus status )
	{
		this.status = status;
	}

	public WebServicesException()
	{
	}

	public WebServicesException( String message, HttpResponseStatus status )
	{
		super(message);
		this.status = status;
	}

	public WebServicesException( Throwable cause, HttpResponseStatus status )
	{
		super(cause);
		this.status = status;
	}

	public WebServicesException( String message, Throwable cause, HttpResponseStatus status )
	{
		super(message, cause);
		this.status = status;
	}

	public WebServicesException( String message )
	{
		super(message);
		this.status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
	}

	public WebServicesException( Throwable cause )
	{
		super(cause);
		this.status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
	}

	public WebServicesException( String message, Throwable cause )
	{
		super(message, cause);
		this.status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
	}

	public HttpResponseStatus getStatus()
	{
		return status;
	}

	public void setStatus( HttpResponseStatus status )
	{
		this.status = status;
	}
}
