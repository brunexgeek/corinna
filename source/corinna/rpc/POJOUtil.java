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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class POJOUtil
{
	
	public static boolean isValidPOJO( Class<?> classRef )
	{
		boolean includeSuperClass = classRef.getClassLoader() != null;

		String name = "";
		String key = "";
		Class<?> returnType = null;
		
		Method[] methods = includeSuperClass ? classRef.getMethods() : classRef.getDeclaredMethods();
		for (Method current : methods)
		{
			name = current.getName();
			key = "";
			
			// check whether the current method is a getter
			if (name.startsWith("get"))
			{
				if (!"getClass".equals(name) && !"getDeclaringClass".equals(name))
					key = "";
				else
					key = name.substring(3);
			}
			else
				if (name.startsWith("is"))
					key = name.substring(2);
				else
					continue;
			// check whether the current getter is a valid POJO getter
			if (key.length() == 0 || Character.isLowerCase(key.charAt(0))
				|| current.getParameterTypes().length > 0)
				continue;
			returnType = current.getReturnType();
			// find for the corresponding setter
			try
			{
				classRef.getMethod("set" + key, returnType);
				return true;
			} catch (Exception e)
			{
				continue;
			}
		}
		
		return false;
	}
	
	public static List<String> getPOJOKeys( Class<?> classRef )
	{
		List<String> list = new LinkedList<String>();

		String name = "";
		String key = "";
		Class<?> returnType = null;
		
		Method[] methods = getMethods(classRef);
		for (Method current : methods)
		{
			name = current.getName();
			key = "";
			
			// check whether the current method is a getter
			if (name.startsWith("get"))
			{
				if ("getClass".equals(name) || "getDeclaringClass".equals(name))
					key = "";
				else
					key = name.substring(3);
			}
			else
				if (name.startsWith("is"))
					key = name.substring(2);
				else
					continue;
			// check whether the current getter is a valid POJO getter
			if (key.length() == 0 || Character.isLowerCase(key.charAt(0))
				|| current.getParameterTypes().length > 0)
				continue;
			returnType = current.getReturnType();
			// find for the corresponding setter
			try
			{
				classRef.getMethod("set" + key, returnType);
				list.add(key);
			} catch (Exception e)
			{
				continue;
			}
		}
		
		return list;
	}
	
	// TODO: create a cache for already processed types
	public static Map<String,POJOInfo> getPOJOInfo( Class<?> classRef )
	{
		Map<String,POJOInfo> list = new HashMap<String, POJOInfo>();

		String name = "";
		String key = "";
		Class<?> returnType = null;
		Method setter = null;
		
		Method[] methods = getMethods(classRef);
		for (Method current : methods)
		{
			name = current.getName();
			key = "";
			
			// check whether the current method is a getter
			if (name.startsWith("get"))
			{
				if ("getClass".equals(name) || "getDeclaringClass".equals(name))
					key = "";
				else
					key = name.substring(3);
			}
			else
				if (name.startsWith("is"))
					key = name.substring(2);
				else
					continue;
			// check whether the current getter is a valid POJO getter
			if (key.length() == 0 || Character.isLowerCase(key.charAt(0))
				|| current.getParameterTypes().length > 0)
				continue;
			returnType = current.getReturnType();
			// find for the corresponding setter
			try
			{
				setter = classRef.getMethod("set" + key, returnType);
				list.put(key, new POJOInfo(key, returnType, current, setter));
			} catch (Exception e)
			{
				continue;
			}
		}
		
		return list;
	}
	
	public static Method getGetter( Method methods[], String key )
	{
		if (key == null || key.isEmpty() || methods == null || methods.length == 0) return null;
		
		String nameIs = "is" + key;
		String nameGet = "get" + key;
		if (Character.isLowerCase(key.charAt(0)))
		{
			nameIs = "is" + Character.toUpperCase(key.charAt(0)) + key.substring(1);
			nameGet = "get" + Character.toUpperCase(key.charAt(0)) + key.substring(1);
		}
		
		for (Method current : methods)
			if (current.getName().equals(nameIs) || current.getName().equals(nameGet))
				return current;
		return null;
	}
	
	public static Method getSetter( Method methods[], String key )
	{
		if (key == null || key.isEmpty() || methods == null || methods.length == 0) return null;
	
		String nameSet = "set" + key;
		if (Character.isLowerCase(key.charAt(0)))
			 nameSet = "set" + Character.toUpperCase(key.charAt(0)) + key.substring(1);
		
		for (Method current : methods)
			if (current.getName().equals(nameSet)) return current;
		return null;
	}

	/**
	 * Returns all methods for the given class.
	 * 
	 * @param classRef
	 * @return
	 */
	public static Method[] getMethods( Class<?> classRef )
	{
		if (classRef.getClassLoader() != null)
			return classRef.getMethods();
		else
			return classRef.getDeclaredMethods();
	}
	
}
