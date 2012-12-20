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

package corinna.bean;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import corinna.thread.ObjectLocker;


public class BeanManager
{

	private static final Logger log = LoggerFactory.getLogger(BeanManager.class);
	
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
	
	public void inject( Object object )
	{
		if (object == null || object.getClass() == Object.class) return;
		
		// iterate in object fields to find anyone that has the BeanInject attribute
		Field fields[] = object.getClass().getDeclaredFields();
		for (Field current : fields)
		{
			BeanInject att = current.getAnnotation(BeanInject.class);
			if (att == null) continue;
			IServiceBean bean = getBean(att.value());
			
			// check if the current field has a compatible type
			//if (!bean.getClass().isAssignableFrom(current.getClass())) continue;
			
			try
			{
				synchronized (current)
				{
					boolean state = current.isAccessible();
					if (!state) current.setAccessible(true);
					current.set(object, bean);
					if (!state) current.setAccessible(false);
					
					//log.debug("Service bean '{}' injected in '{}'", bean.getName(), object.toString() );
				}
			} catch (Exception e)
			{
				log.error("Fail to inject service bean '{}' in '{}'", bean.getName(), object.toString() );
			}
		}
	}
	
}
