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

package corinna.bean;

import java.util.HashMap;
import java.util.Map;

import corinna.thread.ObjectLocker;


public class BeanManager
{

	private Map<String,IServiceBean> beans;
	
	private ObjectLocker beanLock;
	
	private static Boolean instanceLock = false;
	
	private static BeanManager instance = null;
	
	private BeanManager()
	{
		beanLock = new ObjectLocker();
		beans = new HashMap<String,IServiceBean>();
	}
	
	public static BeanManager getInstance()
	{
		synchronized (instanceLock)
		{
			if (instance == null) instance = new BeanManager();
			return instance;
		}
	}
	
	public IServiceBean getBean( String name )
	{
		if (name == null || name.isEmpty()) return null;
		
		beanLock.readLock();
		IServiceBean bean = beans.get(name);
		beanLock.readUnlock();
		
		return bean;
	}

	public IServiceBean removeBean( String name )
	{
		if (name == null || name.isEmpty()) return null;
			
		beanLock.writeLock();
		IServiceBean bean = beans.remove(name);
		beanLock.writeUnlock();
		
		return bean;
	}
	
	public void addBean( IServiceBean bean )
	{
		if (bean == null) return;
		
		beanLock.writeLock();
		beans.put(bean.getName(), bean);
		beanLock.writeUnlock();
	}
	
}
