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

package corinna.bindlet;


import java.util.HashMap;
import java.util.Map;

import javax.bindlet.IObjectSharing;

import corinna.thread.ObjectLocker;


public class ObjectSharing implements IObjectSharing
{

	private Map<String, Object> shared;

	private ObjectLocker sharedLock;

	public ObjectSharing()
	{
		this.shared = new HashMap<String, Object>();
		this.sharedLock = new ObjectLocker();
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
			return (String[]) shared.keySet().toArray();
		} finally
		{
			sharedLock.readUnlock();
		}
	}

	@Override
	public void setSharedObject( String name, Object value )
	{
		if (name == null) throw new NullPointerException("The shared object name can not be null");

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

}
