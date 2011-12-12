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

import java.util.HashMap;
import java.util.Map;

import javax.bindlet.IBindletContext;
import javax.bindlet.ILogger;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;

import corinna.core.IContext;
import corinna.thread.ObjectLocker;
import corinna.util.ApacheLogger;


public abstract class BindletContext<R,P> implements IBindletContext
{
	
	private Map<String,Object> shared;
	
	private ObjectLocker sharedLock;
	
	private IContext<R,P> context;

	private ILogger logger = null;
	
	public BindletContext( IContext<R,P> context )
	{
		if (context == null)
			throw new NullPointerException("The context object can not be null");
		
		this.context = context;
		this.shared = new HashMap<String, Object>();
		this.sharedLock = new ObjectLocker();
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
	public Object getSharedObject( String name )
	{
		if (name == null) return null;
		
		sharedLock.readLock();
		try
		{
			return shared.get(name);		
		} finally
		{
			sharedLock.readUnlock();
		}
	}

	@Override
	public String[] getSharedObjectNames()
	{
		sharedLock.readLock();
		try
		{
			if (shared.isEmpty()) return null;
			return (String[])shared.keySet().toArray();		
		} finally
		{
			sharedLock.readUnlock();
		}
	}

	@Override
	public void setSharedObject( String name, Object value )
	{
		if (name == null)
			throw new NullPointerException("The shared object name can not be null");
		
		sharedLock.writeLock();
		try
		{
			shared.put(name, value);			
		} finally
		{
			sharedLock.writeUnlock();
		}
	}

	@Override
	public Object removeSharedObject( String name )
	{
		if (name == null) return null;
		
		sharedLock.writeLock();
		try
		{
			return shared.remove(name);			
		} finally
		{
			sharedLock.writeUnlock();
		}
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
	
}
