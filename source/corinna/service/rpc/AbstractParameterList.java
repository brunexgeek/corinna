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

package corinna.service.rpc;


import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;

import corinna.exception.EntryNotFoundException;

/**
 * 
 * This class is not thread-safe.
 * 
 * @author bruno
 *
 */
public abstract class AbstractParameterList implements Serializable, IParameterList
{

	private static final long serialVersionUID = 3151063470751380802L;

	protected HashMap<String, Object> parameters;

	protected String[] parameterNames;
	
	protected Charset charset = null;
	
	private boolean isChanged = false;

	public AbstractParameterList()
	{
		this( Charset.defaultCharset() );
	}

	public AbstractParameterList( Charset charset )
	{
		if (charset == null)
			throw new IllegalArgumentException("The charset can not be null");
		
		this.parameters = new HashMap<String, Object>();
		this.parameterNames = new String[0];
		this.charset = charset;
	}

	@Override
	public boolean containsParameter( String key )
	{
		return parameters.containsKey(key);
	}
	
	@Override
	public Object getParameter( String key, Object defaultValue )
	{
		Object value = parameters.get(key);
		if (value == null) return defaultValue;
		return value;
	}

	public void setValue( String key, Object value )
	{
		if (value == null)
			parameters.remove(key);
		else
			parameters.put(key, value);
		isChanged = true;
	}

	@Override
	public Object getParameter( String key ) throws EntryNotFoundException
	{
		Object value = parameters.get(key);
		if (value == null) throw new EntryNotFoundException("Key '" + key + "' not found");
		return value;
	}

	@Override
	public Charset getCharset()
	{
		return charset;
	}
	
	@Override
	public String[] getParameterNames()
	{
		if (isChanged)
		{
			parameterNames = parameters.keySet().toArray( new String[0] );
			isChanged = false;
		}
		return parameterNames;
	}
	
}
