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
package corinna.rpc;

import java.lang.reflect.Method;


public class POJOInfo
{
	
	private String suffix;
	private Method getter;
	private Method setter;
	private Class<?> type;

	protected POJOInfo( String suffix, Class<?> type, Method getter, Method setter )
	{
		this.suffix = suffix;
		this.getter = getter;
		this.setter = setter;
		this.type = type;
	}

	public String getSuffix()
	{
		return suffix;
	}

	public Method getGetter()
	{
		return getter;
	}

	public Method getSetter()
	{
		return setter;
	}

	public Class<?> getType()
	{
		return type;
	}
}