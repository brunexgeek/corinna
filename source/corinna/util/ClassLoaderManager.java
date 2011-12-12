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

package corinna.util;

import java.util.LinkedList;
import java.util.List;

import corinna.thread.ObjectLocker;


public final class ClassLoaderManager
{
	
	private static List<ClassLoader> loaders;
	
	private static ObjectLocker lock;
	
	static
	{
		lock = new ObjectLocker();
		loaders = new LinkedList<ClassLoader>();
		add( ClassLoaderManager.class.getClassLoader() );
	}
	
	/**
	 * Registra um novo <code>ClassLoader</code>. Quando um recurso é solicitado, todos os 
	 * <i>class loaders</i> registrados são consultados.
	 * <br/><br/>
	 * Por padrão, o único <code>ClassLoader</code> registrado é o mesmo utilizado pelo framework.
	 * @param loader Instância {@link ClassLoader} do <i>class loader</i> a ser incluído.
	 */
	public static boolean add( ClassLoader loader )
	{
		lock.writeLock();
		try
		{
			return loaders.add(loader);
		} catch (Exception e)
		{
			return false;
		} finally
		{
			lock.writeUnlock();
		}
	}
	
	public static void remove( ClassLoader loader )
	{
		lock.writeLock();
		try
		{
			loaders.remove(loader);
		} catch (Exception e)
			// supress errors
		{
		} finally
		{
			lock.writeUnlock();
		}
	}
	
	public static ClassLoader get( int index )
	{
		lock.readLock();
		try
		{
			return loaders.get(index);
		} catch (Exception e)
		{
			return null;
		} finally
		{
			lock.readUnlock();
		}
	}
	
	public static int size()
	{
		lock.readLock();
		try
		{
			return loaders.size();
		} finally
		{
			lock.readUnlock();
		}
	}
}
