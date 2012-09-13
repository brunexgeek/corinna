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

package corinna.core;


import java.util.HashMap;
import java.util.Map;

import javax.bindlet.BindletConfig;
import javax.bindlet.BindletModel;
import javax.bindlet.BindletModel.Model;
import javax.bindlet.IBindlet;
import javax.bindlet.IBindletConfig;
import javax.bindlet.IRecyclable;
import javax.bindlet.exception.BindletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import corinna.bean.BeanManager;
import corinna.thread.ObjectLocker;
import corinna.util.ObjectPool;

/**
 * 
 * @author Bruno Ribeiro
 * @version 1.0
 * @since 1.0
 * 
 * @see IBindletConfig
 * @see IContext
 */
public class BindletRegistration implements IBindletRegistration
{

	private static final int DEFAULT_CAPACITY = 20;
	
	private Logger serverLog = LoggerFactory.getLogger(BindletRegistration.class);
	
	private IBindletConfig config = null;

	private Boolean isModified = false;

	private Map<String, String> parameters;

	private String bindletName;

	private IContext<?, ?> context;

	private String bindletClassName;

	private boolean isUnavailable = false;

	private boolean isLoadOnStartup = false;

	private Class<? extends IBindlet<?,?>> bindletClass;

	private Model bindletModel = Model.STATEFULL;
	
	private IBindlet<?,?> bindletInstance = null;
	
	private ObjectLocker bindletInstanceLock = null;
	
	private ObjectPool<IBindlet<?,?>> bindlets = null;

	@SuppressWarnings("unchecked")
	public BindletRegistration( IContext<?, ?> context, String bindletName, String bindletClassName )
		throws ClassNotFoundException, BindletException
	{
		if (context == null)
			throw new IllegalArgumentException("The context object can not be null");
		if (bindletName == null || bindletName.isEmpty())
			throw new IllegalArgumentException("The bindlet name can not be null or empty");
		if (bindletClassName == null || bindletClassName.isEmpty())
			throw new IllegalArgumentException("The bindlet class name can not be null or empty");

		this.context = context;
		this.bindletName = bindletName;
		this.bindletClassName = bindletClassName;
		this.parameters = new HashMap<String,String>();
		this.bindletInstanceLock = new ObjectLocker();
		this.bindlets = new ObjectPool<IBindlet<?,?>>(DEFAULT_CAPACITY);

		// try to load the bindlet class
		try
		{
			this.bindletClass = (Class<? extends IBindlet<?, ?>>) Class.forName(bindletClassName);
		} catch (Exception e)
		{
			throw new BindletException("Error loading bindlet class", e);
		}
		// check the bindlet model
		BindletModel annot = this.bindletClass.getAnnotation(BindletModel.class);
		if (annot != null) bindletModel = annot.value();
		// ensures that the bindlet implements IRecyclable if was marked with this model
		if (bindletModel == Model.RECYCLABLE && !IRecyclable.class.isAssignableFrom(bindletClass))
			throw new BindletException("Recyclable bindlets must be implements 'IRecyclable'"
				+ " interface");
	}

	protected void setModified( boolean value )
	{
		synchronized (isModified)
		{
			isModified = value;
		}
	}

	protected boolean isModified()
	{
		synchronized (isModified)
		{
			return isModified;
		}
	}

	@Override
	public Class<? extends IBindlet<?, ?>> getBindletClass()
	{
		return bindletClass;
	}

	@Override
	public String getBindletClassName()
	{
		return bindletClassName;
	}

	@Override
	public boolean isUnavailable()
	{
		return isUnavailable;
	}

	@Override
	public IBindlet<?, ?> createBindlet() throws BindletException
	{
		IBindlet<?,?> instance = null;
		
		if (bindletModel == Model.STATELESS) 
			instance = getBindletInstance();
		else
		if (bindletModel == Model.RECYCLABLE)
			instance = borrowBindletInstance();
		else
			instance = createBindletInstance();
		
		return instance;
	}

	@Override
	public void releaseBindlet( IBindlet<?, ?> bindlet ) throws BindletException
	{
		if (bindlet == null) return;
		
		if (bindletClass.isAssignableFrom(bindlet.getClass()) && bindletModel == Model.RECYCLABLE)
		{
			((IRecyclable)bindlet).recycle();
			bindlets.back(bindlet);
		}
	}
	
	protected IBindlet<?,?> borrowBindletInstance() throws BindletException
	{
		IBindlet<?,?> bindlet = bindlets.borrow();
		if (bindlet == null)
			return createBindletInstance();
		else
			return bindlet;
	}
	
	protected IBindlet<?,?> getBindletInstance() throws BindletException
	{
		IBindlet<?,?> instance;
		
		bindletInstanceLock.readLock();
		instance = bindletInstance;
		bindletInstanceLock.readUnlock();
		
		if (instance == null)
		{
			instance = createBindletInstance();
			bindletInstanceLock.writeLock();
			bindletInstance = instance;
			bindletInstanceLock.writeUnlock();
		}
		
		return instance;
	}
	
	protected IBindlet<?,?> createBindletInstance() throws BindletException
	{
		IBindlet<?,?> instance;

		try
		{
			instance = (IBindlet<?, ?>) bindletClass.newInstance();
		} catch (Exception e)
		{
			throw new BindletException("Error creating a new instance of bindlet class '"
				+ bindletClassName + "'", e);
		}
	
		serverLog.trace("Created bindlet instance");
		
		// inject all referenced service beans
		BeanManager.getInstance().inject(instance);
		
		return instance;
	}
	
	@Override
	public boolean isLoadOnStartup()
	{
		return isLoadOnStartup;
	}

	@Override
	public IBindletConfig getBindletConfig()
	{
		if (isModified())
		{
			config = new BindletConfig(bindletName, context.getBindletContext(), parameters);
			setModified(false);
		}
		return config;
	}

	@Override
	public String getBindletParameter( String name )
	{
		return parameters.get(name);
	}

	@Override
	public void setBindletParameter( String name, String value )
	{
		parameters.put(name, value);
		setModified(true);
	}

	@Override
	public void setUnavailable( boolean value )
	{
		if (value != isUnavailable)
		{
			isUnavailable = value;
			setModified(true);
		}
	}

	@Override
	public void setLoadOnStartup( boolean value )
	{
		if (value != isLoadOnStartup)
		{
			isLoadOnStartup = value;
			setModified(true);
		}
	}

	@Override
	public BindletModel.Model getBindletModel()
	{
		return bindletModel;
	}

	@Override
	public String getBindletName()
	{
		return bindletName;
	}

	@Override
	public String toString()
	{
		return "        Bindlet '" + getBindletName() + "' [" + 
			getBindletClass().getCanonicalName() + "]\n";
	}
	
}
