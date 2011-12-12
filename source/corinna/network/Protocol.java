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

package corinna.network;

import java.util.HashMap;
import java.util.Map;

import corinna.util.Reflection;


public abstract class Protocol<R,P> implements IProtocol<R,P>
{
	
	private Map<String,Boolean> capabilities;

	protected Protocol()
	{
		capabilities = new HashMap<String,Boolean>();
	}
	
	@Override
	public boolean haveCapability( String capability )
	{
		return capability.contains(capability);
	}
	
	protected void addCapacility( String capability, Boolean status )
	{
		capabilities.put(capability, status);
	}

	@Override
	public String toString()
	{
		return getName() + "/" + getVersion();
	}
	
	@Override
	public Class<?> getRequestClass()
	{
		return Reflection.getGenericParameter(this, Protocol.class, 0);
	}
	
	@Override
	public Class<?> getResponseClass()
	{
		return Reflection.getGenericParameter(this, Protocol.class, 1);
	}

}
