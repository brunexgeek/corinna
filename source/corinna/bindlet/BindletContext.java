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

package corinna.bindlet;

import javax.bindlet.IBindletContext;
import javax.bindlet.ILogger;

import corinna.core.IContext;
import corinna.util.ApacheLogger;
import corinna.util.IComponentInformation;


public abstract class BindletContext<R,P> extends ObjectSharing implements IBindletContext
{
		
	private IContext<R,P> context;

	private ILogger logger = null;
	
	public BindletContext( IContext<R,P> context )
	{
		if (context == null)
			throw new NullPointerException("The context object can not be null");
		
		this.context = context;
	}
	
	@Override
	public String getContextParameter( String name )
	{
		return context.getParameter(name);
	}

	@Override
	public String[] getContextParameterNames()
	{
		return context.getParameterNames();
	}

	@Override
	public String getContextName()
	{
		return context.getName();
	}
	
	@Override
	public ILogger getLogger()
	{
		if (logger  == null)
			logger = new ApacheLogger(this.getClass());
		return logger;
	}
	
	@Override
	public Class<?> getContextRequestType()
	{
		return context.getRequestType();
	}

	@Override
	public Class<?> getContextResponseType()
	{
		return context.getResponseType();
	}
	
	protected IContext<?,?> getContext()
	{
		return context;
	}
	
	@Override
	public IComponentInformation getContextInfo()
	{
		return context.getContextInfo();
	}
	
}
