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

package corinna.http.core;


import javax.bindlet.IBindletContext;
import javax.bindlet.IComponentInformation;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;

import corinna.core.IContextConfig;
import corinna.core.IService;
import corinna.http.bindlet.HttpBindletContext;


public class HttpContext extends WebContext<IHttpBindletRequest,IHttpBindletResponse>
{
	
	private HttpBindletContext bindletContext = null;
	
	public HttpContext( IContextConfig config, IService service )
	{
		super(config, service);
	}

	@Override
	public IComponentInformation getContextInfo()
	{
		return HttpContextInfo.getInstance();
	}


	@Override
	protected IBindletContext createBindletContext()
	{
		if (bindletContext == null)
			bindletContext = new HttpBindletContext(this);
		return bindletContext;
	}
	
	@Override
	public Class<?> getRequestType()
	{
		return IHttpBindletRequest.class;
	}
	
	@Override
	public Class<?> getResponseType()
	{
		return IHttpBindletResponse.class;
	}
	
}
