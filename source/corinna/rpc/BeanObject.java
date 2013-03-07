package corinna.rpc;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import corinna.exception.BeanObjectException;


public class BeanObject implements IBeanObject
{

	Class<?> PRIMITIVES[] = { Long.class, Integer.class, Short.class, Byte.class, Character.class,
		String.class, Double.class, Float.class, Boolean.class };
	
	protected Map<String, Object> entries;

	public BeanObject( Object source )
	{
		this();
		extract(source);
	}

	public BeanObject()
	{
		entries = new HashMap<String, Object>();
	}

	@Override
	public Object get( String key )
	{
		if (key == null) return null;
		return entries.get(key);
	}

	@Override
	public void set( String key, Object value )
	{
		if (key == null) return;
		entries.put(key, value);
	}
	
	@Override
	public void setString( String key, String value )
	{
		set(key, value);
	}
	
	@Override
	public boolean getBoolean( String key ) throws BeanObjectException
	{
		Object object = this.get(key);
		if (object == null) throw new BeanObjectException("Key [" + key + "] can not be null.");

		if (object instanceof Boolean) return (Boolean) object;

		String value = object.toString();

		if (value.equalsIgnoreCase("false")) return false;
		if (value.equalsIgnoreCase("true")) return true;

		throw new BeanObjectException("Value [" + value + "] is not a Boolean.");
	}

	@Override
	public double getDouble( String key ) throws BeanObjectException
	{
		Object object = this.get(key);
		if (object == null) throw new BeanObjectException("Key [" + key + "] can not be null.");

		try
		{
			if (object instanceof Number)
				return ((Number) object).doubleValue();
			else
				return Double.parseDouble(object.toString());
		} catch (Exception e)
		{
			throw new BeanObjectException("Key [" + key + "] is not a number.");
		}
	}

	@Override
	public float getFloat( String key ) throws BeanObjectException
	{
		Object object = this.get(key);
		if (object == null) throw new BeanObjectException("Key [" + key + "] can not be null.");

		try
		{
			if (object instanceof Number)
				return ((Number) object).floatValue();
			else
				return Float.parseFloat(object.toString());
		} catch (Exception e)
		{
			throw new BeanObjectException("Key [" + key + "] is not a number.");
		}
	}

	@Override
	public int getInt( String key ) throws BeanObjectException
	{
		Object object = this.get(key);
		if (object == null) throw new BeanObjectException("Key [" + key + "] can not be null.");

		try
		{
			if (object instanceof Number)
				return ((Number) object).intValue();
			else
				return Integer.parseInt(object.toString());
		} catch (Exception e)
		{
			throw new BeanObjectException("Key [" + key + "] is not a number.");
		}
	}

	@Override
	public short getShort( String key ) throws BeanObjectException
	{
		Object object = this.get(key);
		if (object == null) throw new BeanObjectException("Key [" + key + "] can not be null.");

		try
		{
			if (object instanceof Number)
				return ((Number) object).shortValue();
			else
				return Short.parseShort(object.toString());
		} catch (Exception e)
		{
			throw new BeanObjectException("Key [" + key + "] is not a number.");
		}
	}

	@Override
	public byte getByte( String key ) throws BeanObjectException
	{
		Object object = this.get(key);
		if (object == null) throw new BeanObjectException("Key [" + key + "] can not be null.");

		try
		{
			if (object instanceof Number)
				return ((Number) object).byteValue();
			else
				return Byte.parseByte(object.toString());
		} catch (Exception e)
		{
			throw new BeanObjectException("Key [" + key + "] is not a number.");
		}
	}

	@Override
	public long getLong( String key ) throws BeanObjectException
	{
		Object object = this.get(key);
		if (object == null) throw new BeanObjectException("Key [" + key + "] can not be null.");

		try
		{
			if (object instanceof Number)
				return ((Number) object).longValue();
			else
				return Long.parseLong(object.toString());
		} catch (Exception e)
		{
			throw new BeanObjectException("Key [" + key + "] is not a number.");
		}
	}

	@Override
	public String getString( String key ) throws BeanObjectException
	{
		Object object = this.get(key);
		if (object == null) throw new BeanObjectException("Key [" + key + "] can not be null.");

		return object.toString();
	}

	@Override
	public IBeanObject getObject( String key ) throws BeanObjectException
	{
		Object object = this.get(key);
		if (object == null) throw new BeanObjectException("Key [" + key + "] can not be null.");

		if (object instanceof IBeanObject)
			return (IBeanObject) object;
		else
			throw new BeanObjectException("Key [" + key + "] is not a number.");
	}

	@Override
	public Iterator<String> keys()
	{
		return entries.keySet().iterator();
	}

	protected String extractPOJOGetterSuffix( Class<?> classRef, Method method )
	{
		if (method == null) return null;

		String name = method.getName();
		String key = "";
		Class<?> returnType;

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
				return null;
		// check whether the current getter is a valid POJO getter
		if (key.length() == 0 || Character.isLowerCase(key.charAt(0))
			|| method.getParameterTypes().length > 0) return null;
		returnType = method.getReturnType();
		// find for the corresponding setter
		try
		{
			classRef.getMethod("set" + key, returnType);
			return key;//Character.toLowerCase( key.charAt(0) ) + key.substring(1);
		} catch (Exception e)
		{
			return null;
		}
	}

	protected Method getMethod( Class<?> classRef, String name, Class<?> ... params )
	{
		try
		{
			return classRef.getMethod(name, params);
		} catch (Exception e)
		{
			return null;
		}
	}
	
	// TODO: optimize string manipulation
	protected String extractPOJOSetterSuffix( Class<?> classRef, Method method )
	{
		if (method == null) return null;

		String name = method.getName();
		String key = "";
		Class<?> returnType;

		// check whether the current method is a getter
		if (name.startsWith("set"))
			key = name.substring(3);
		else
			return null;
		// check whether the current getter is a valid POJO getter
		if (key.length() == 0 || Character.isLowerCase(key.charAt(0))
			|| method.getParameterTypes().length != 1) return null;
		returnType = method.getParameterTypes()[0];
		// find for the corresponding getter
		Method getter = getMethod(classRef, "get" + key);
		if (getter == null)
			getter = getMethod(classRef, "is" + key);
		if (getter != null && getter.getReturnType() == returnType)
			return key;//Character.toLowerCase( key.charAt(0) ) + key.substring(1);
		else
			return null;
	}
	
	protected Object callPOJOGetter( Object bean, Method method )
	{
		if (method == null) return null;
		try
		{
			return method.invoke(bean);
		} catch (Exception e)
		{
			return null;
		}
	}

	protected void callPOJOSetter( Object bean, Method method, Object value )
	{
		if (method == null) return;
		try
		{
			method.invoke(bean, value);
		} catch (Exception e)
		{
		}
	}
	
	public static boolean isPrimitive( Class<?> classRef )
	{
		if (classRef == Long.class) return true;
		if (classRef == Integer.class) return true;
		if (classRef == Short.class) return true;
		if (classRef == Byte.class) return true;
		if (classRef == Double.class) return true;
		if (classRef == Float.class) return true;
		if (classRef == Character.class) return true;
		if (classRef == String.class) return true;
		if (classRef == Boolean.class) return true;
		if (Enum.class.isAssignableFrom(classRef)) return true;
		return false;
	}

	@Override
	public void extract( Object source )
	{
		if (source == null) return;
		Class<?> classRef = source.getClass();
		
		Method[] methods = POJOUtil.getMethods(classRef);
		for (Method current : methods)
		{
			String suffix = extractPOJOGetterSuffix(classRef, current);
			if (suffix == null) continue;
			
			Object value = callPOJOGetter(source, current);
			if (value != null && !isPrimitive(value.getClass()))
			{
				IBeanObject object = new BeanObject(value);
				entries.put(suffix, object);
			}
			else
				entries.put(suffix, value);
		}
	}

	@Override
	public void populate( Object destination )
	{
		if (destination == null) return;
		Class<?> classRef = destination.getClass();
		Object bean = null;
		
		Method[] methods = POJOUtil.getMethods(classRef);
		for (Method current : methods)
		{
			String suffix = extractPOJOSetterSuffix(classRef, current);
			if (suffix == null) continue;
			
			Object value = entries.get(suffix);
			if (value != null && IBeanObject.class.isAssignableFrom(value.getClass()))
			{
				// create a new instance of the java bean
				try
				{
					bean = current.getParameterTypes()[0].newInstance();
					((IBeanObject)value).populate(bean);
				} catch (Exception e)
				{
					continue;
				}
				callPOJOSetter(destination, current, bean);
			}
			else
			{
				// check whether the value type match with setter parameter
				Class<?> paramType = current.getParameterTypes()[0];
				if (value != null && !paramType.equals(value.getClass()))
					value = TypeConverter.convert(paramType, value);
				callPOJOSetter(destination, current, value);
			}
		}
	}

}
