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

package corinna.core;

import corinna.exception.ConfigurationNotFoundException;
import corinna.util.conf.ISection;
import corinna.util.conf.Section;


public abstract class BasicConfig implements IBasicConfig
{

	protected ISection section;
	
	protected String name;

	public BasicConfig( String name )
	{
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("The name can not be null or empty");
		
		this.name = name;
		section = new Section(name + "Parameters", true);
	}
	
	@Override
	public String getParameter( String name ) throws ConfigurationNotFoundException
	{
		return section.getString(name);
	}

	@Override
	public String[] getParameterNames()
	{
		return section.getKeys();
	}

	@Override
	public ISection getSection()
	{
		return section;
	}

	@Override
	public String getParameter( String name, String defaultValue )
	{
		return section.getString(name, defaultValue);
	}

	@Override
	public void setParameter( String name, String value )
	{
		section.setValue(name, value);
	}
	
	@Override
	public boolean containsParameter( String name )
	{
		return section.containsKey(name);
	}
	
}
