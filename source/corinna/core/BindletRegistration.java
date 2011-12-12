package corinna.core;


import java.util.HashMap;
import java.util.Map;

import javax.bindlet.BindletConfig;
import javax.bindlet.BindletModel;
import javax.bindlet.BindletModel.Model;
import javax.bindlet.IBindlet;
import javax.bindlet.IBindletConfig;
import javax.bindlet.IRecyclable;

import corinna.exception.BindletException;

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
		if (annot != null) bindletModel = annot.model();
		// ensures that the bindlet implements IRecyclable if was marked with this model
		if (bindletModel == Model.RECYCLABLE && !bindletClass.isAssignableFrom(IRecyclable.class))
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
		try
		{
			return (IBindlet<?, ?>) bindletClass.newInstance();
		} catch (Exception e)
		{
			throw new BindletException("Error creating a new instance of bindlet class '"
				+ bindletClassName + "'", e);
		}
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

}
