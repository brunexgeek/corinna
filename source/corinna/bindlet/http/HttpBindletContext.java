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

package corinna.bindlet.http;

import javax.bindlet.http.IHttpBindletContext;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;

import corinna.bindlet.BindletContext;
import corinna.core.http.HttpContext;


public class HttpBindletContext extends BindletContext<IHttpBindletRequest, IHttpBindletResponse> 
	implements IHttpBindletContext
{
	
	public HttpBindletContext( HttpContext context )
	{
		super(context);
	}
	
}
