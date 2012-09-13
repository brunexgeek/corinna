/*
 * Copyright 2011-2012 Bruno Ribeiro
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

package corinna.http.core;

import javax.bindlet.IComponentInformation;


public class HttpContextInfo implements IComponentInformation
{

	private static IComponentInformation instance = null;
	
	public static IComponentInformation getInstance()
	{
		if (instance == null) instance = new HttpContextInfo();
		return instance;
	}
	
	@Override
	public String getComponentName()
	{
		return "HTTP Context";
	}

	@Override
	public String getComponentVersion()
	{
		return "1.0";
	}

	@Override
	public String getComponentImplementor()
	{
		return "Bruno Ribeiro";
	}
	
}