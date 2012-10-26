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

	public static Method[] getMethods( Class<?> classRef )
	{
		if (classRef.getClassLoader() != null)
			return classRef.getMethods();
		else
			return classRef.getDeclaredMethods();
	}
	
}
