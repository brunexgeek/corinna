/*
 * Copyright 2011-2012 Bruno Ribeiro>
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

package corinna.http.network;

import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;

import corinna.network.RequestEvent;


public class HttpRequestEvent extends
	RequestEvent<IHttpBindletRequest, IHttpBindletResponse>
{

	private static final long serialVersionUID = -1125203305586209689L;

	public HttpRequestEvent( IHttpBindletRequest request, IHttpBindletResponse response )
	{
		super(request, response);
	}

}
