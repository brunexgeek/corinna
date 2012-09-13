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


import javax.bindlet.http.IWebBindletRequest;
import javax.bindlet.http.IWebBindletResponse;

import corinna.core.Context;
import corinna.core.IBindletRegistration;
import corinna.core.IContextConfig;
import corinna.core.IService;
import corinna.http.bindlet.WebBindletRequest;

public abstract class WebContext<R extends IWebBindletRequest, P extends IWebBindletResponse>
	extends Context<R, P> implements IWebContext<R, P>
{

	private static final String BINDLET_URL_MAPPING = "urlMapping";

	private static final String CONTEXT_URL_MAPPING = "urlMapping";

	public WebContext( IContextConfig config, IService service )
	{
		super(config, service);
	}

	protected IBindletRegistration findRegistration( IWebBindletRequest request )
	{
		IBindletRegistration[] regs = getBindletRegistrations();
		String path = request.getResourcePath();

		for (IBindletRegistration current : regs)
		{
			String pattern = current.getBindletParameter(BINDLET_URL_MAPPING);

			if (pattern == null || path == null) continue;

			try
			{
				String value = HttpUtils.matchURI(pattern, path);
				if (value == null) continue;

				if (request instanceof WebBindletRequest)
					((WebBindletRequest) request).setBindletPath(value);
			} catch (Exception e)
			{
				continue;
			}

			return current;
		}

		return null;
	}

	protected boolean matchContextPath( IWebBindletRequest request )
	{
		// check if the URL of the request match with the current context path
		String pattern = getConfig().getParameter(CONTEXT_URL_MAPPING, null);
		if (pattern == null || pattern.isEmpty()) pattern = "/";
		String path = request.getResourcePath();
		if (pattern == null || path == null) return false;

		try
		{
			String value = HttpUtils.matchURI(pattern, path);
			if (value == null) return false;

			if (request instanceof WebBindletRequest)
				((WebBindletRequest) request).setContextPath(value);
		} catch (Exception e)
		{
			return false;
		}

		return true;
	}

	@Override
	public IBindletRegistration getBindletRegistration( IWebBindletRequest request )
	{
		return findRegistration(request);
	}

	@Override
	protected boolean acceptRequest( IWebBindletRequest request )
	{
		return matchContextPath(request);
	}
	
}
