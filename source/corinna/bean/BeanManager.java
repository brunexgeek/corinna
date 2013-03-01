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


/**
 * Manage the service beans of a domain and allow to inject it in any field with {@link BeanInject}
 * annotation.
 * 
 * @author Bruno Ribeiro
 */
public class BeanManager
{

	private static final Logger log = LoggerFactory.getLogger(BeanManager.class);

	/**
	 * List of active service beans.
	 */
	private Map<String, IServiceBean> beans;

	/**
	 * Lock used to protect the access to the {@link #beans} field.
	 */
	private ObjectLocker beanLock;

	/**
	 * Lock used to protect the access to the {@link #instance} field.
	 */
	private static Boolean instanceLock = false;

	/**
	 * Reference to the singleton instance of this class.
	 */
	private static BeanManager instance = null;

	private BeanManager()
	{
		beanLock = new ObjectLocker();
		beans = new HashMap<String, IServiceBean>();
	}

	/**
	 * Returns the single instance of this class.
	 * 
	 * @return
	 */
	public static BeanManager getInstance()
	{
		synchronized (instanceLock)
		{
			if (instance == null) instance = new BeanManager();
			return instance;
		}
	}

	/**
	 * Returns the service bean name. This name is used when injecting a service bean via
	 * {@link BeanInject} annotation.
	 * 
	 * @param name
	 * @return
	 */
	public IServiceBean getBean( String name )
	{
		if (name == null || name.isEmpty()) return null;

		beanLock.readLock();
		IServiceBean bean = beans.get(name);
		beanLock.readUnlock();

		return bean;
	}

	/**
	 * Remove the service bean with the given name from the manager. Once removed, that service can
	 * no longer be injected.
	 * 
	 * @param name
	 * @return
	 */
	public IServiceBean removeBean( String name )
	{
		if (name == null || name.isEmpty()) return null;

		beanLock.writeLock();
		IServiceBean bean = beans.remove(name);
		beanLock.writeUnlock();

		return bean;
	}

	/**
	 * Add the service bean to the manager. Once added, the service bean can be injected.
	 * 
	 * @param bean
	 */
	public void addBean( IServiceBean bean )
	{
		if (bean == null) return;

		beanLock.writeLock();
		beans.put(bean.getName(), bean);
		beanLock.writeUnlock();
	}

	/**
	 * Find all field in the give object that have the {@link BeanIbject} annotation and set its
	 * value with the reference of the respective service bean.
	 * 
	 * @param object
	 *            Object that will receive the injection.
	 */
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
			if (bean == null)
				log.error("Service bean '{}' not found when injecting on '{}'", att.value(), 
					object.getClass().getName());

			try
			{
				synchronized (current)
				{
					boolean state = current.isAccessible();
					if (!state) current.setAccessible(true);
					boolean result = trySetField(object, current, bean);
					if (!result) trySetField(object, current, null);
					if (!state) current.setAccessible(false);
				}
			} catch (Exception e)
			{
				log.error("Fail to inject service bean '{}' in '{}'", att.value(), 
					object.getClass().getName());
			}
		}
	}

	/**
	 * Try set the value of the given class field.
	 * 
	 * @param object
	 *            Object whose field will set.
	 * @param field
	 *            Field will set.
	 * @param value
	 *            New value of the field.
	 * @return <code>true</code> if the value of the field was succefuly setted or
	 *         <code>false</code> otherwise.
	 */
	private boolean trySetField( Object object, Field field, Object value )
	{
		try
		{
			field.set(object, value);
			return true;
		} catch (Exception e)
		{
			return false;
		}
	}

}
