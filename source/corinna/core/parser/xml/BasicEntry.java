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

package corinna.core.parser.xml;

import corinna.core.IBasicConfig;


public class BasicEntry
{

	private IBasicConfig config;

	private String className;

	public BasicEntry( String className, IBasicConfig config )
	{
		if (config == null)
			throw new IllegalArgumentException("The configuration object can not be null");
		if (className == null || className.isEmpty())
			throw new IllegalArgumentException("The class name can not be null or empty");
		
		this.config = config;
		this.className = className;
	}

	public IBasicConfig getConfig()
	{
		return config;
	}

	public String getClassName()
	{
		return className;
	}
	
}
