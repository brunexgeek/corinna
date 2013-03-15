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

package corinna.bindlet;

import javax.bindlet.IBindletContext;
import javax.bindlet.IComponentInformation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import corinna.core.IContext;


public abstract class BindletContext<R,P> extends ObjectSharing implements IBindletContext
{
		
	private IContext<R,P> context;

	private Logger logger = null;
	
	public BindletContext( IContext<R,P> context )
	{
		if (context == null)
			throw new NullPointerException("The context object can not be null");
		
		this.context = context;
	}
	
	@Override
	public String getContextParameter( String name )
	{
		return context.getConfig().getParameter(name, null);
	}

	@Override
	public String[] getContextParameterNames()
	{
		return context.getConfig().getParameterNames();
	}

	@Override
	public String getContextName()
	{
		return context.getName();
	}
	
	@Override
	public Logger getLogger()
	{
		if (logger  == null)
			logger = LoggerFactory.getLogger(this.getClass());
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
