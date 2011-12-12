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

package javax.bindlet;

import java.util.HashMap;
import java.util.Map;

/**
 * A bindlet configuration object used by a bindlet container to pass information to a bindlet
 * during it's initialization.
 * 
 * @author Bruno Ribeiro
 * @version 1.0
 * @since 1.0
 * @see BindletRegistration
 */
public class BindletConfig implements IBindletConfig
{
	
	private Map<String,String> params;

	private IBindletContext context = null;

	private String name = null;
	
	private String[] paramNames = null;
	
	public BindletConfig( String bindletName, IBindletContext context, Map<String,String> parameters )
	{
		if (bindletName == null || bindletName.isEmpty())
			throw new IllegalArgumentException("The bindlet name can not be null or empty");
		if (context == null)
			throw new IllegalArgumentException("The bindlet context is required");
	
		this.name = bindletName;
		this.context = context;
		if (parameters == null)
			this.params = new HashMap<String,String>();
		else
			this.params = new HashMap<String,String>(parameters);
	}
	
	@Override
	public String getBindletName()
	{
		return name;
	}

	@Override
	public IBindletContext getBindletContext()
	{
		return context;
	}

	@Override
	public String getBindletParameter( String name )
	{
		Object value = params.get(name);
		if (value != null) return value.toString();
		return null;
	}
	
	@Override
	public String[] getBindletParameterNames()
	{
		if (paramNames == null) paramNames = params.keySet().toArray( new String[0] );
		return paramNames;
	}
	
}
