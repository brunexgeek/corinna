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

package corinna.network;

import corinna.core.AbstractEvent;


public class RequestEvent<R,P> extends AbstractEvent
{

	private static final long serialVersionUID = 8507321749616247470L;

	private Boolean isHandled = false;
	
	private transient R request;
	
	private transient P response;
	
	public RequestEvent( R request, P response )
	{
		if (request == null)
			throw new NullPointerException("The request can not be null");
		if (response == null)
			throw new NullPointerException("The response can not be null");
		
		this.request = request;
		this.response = response;
	}

	public void setHandled( boolean isHandled )
	{
		synchronized (this.isHandled)
		{
			this.isHandled = isHandled;
		}
	}

	public Boolean isHandled()
	{
		synchronized (this.isHandled)
		{
			return isHandled;
		}
	}

	public P getResponse()
	{
		return response;
	}

	public R getRequest()
	{
		return request;
	}
	
}
