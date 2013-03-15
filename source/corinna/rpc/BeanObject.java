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
import java.util.Collection;
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
	public IBeanCollection getCollection( String key ) throws BeanObjectException
	{
		Object object = this.get(key);
		if (object == null) throw new BeanObjectException("Key [" + key + "] can not be null.");

		if (object instanceof IBeanCollection)
			return (IBeanCollection) object;
		else
			throw new BeanObjectException("Key [" + key + "] is not a number.");
	}
	
	@Override
	public Iterator<String> keys()
	{
		return entries.keySet().iterator();
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
	
	public static boolean isCollection( Class<?> classRef )
	{
		return (Collection.class.isAssignableFrom(classRef));
	}
	
	/**
	 * Returns a logical value indicating whether the given type is primitive. For this
	 * implementationm primitive types are all types that can represented with a
	 * single value (integers, strings, enumerations, etc.).
	 * 
	 * @param classRef
	 * @return
	 */
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
		
		// retrieve all POJO information from destination object
		Map<String, POJOInfo> infoMap = POJOUtil.getPOJOInfo(classRef);
		for (String current : infoMap.keySet())
		{
			POJOInfo info = infoMap.get(current);
			// insert the getter value in the bean object
			Object value = callPOJOGetter(source, info.getGetter());
			value = extractValue(value);
			entries.put(current, value);
		}
	}

	@Override
	public void populate( Object destination )
	{
		if (destination == null) return;
		Class<?> classRef = destination.getClass();
		Object bean = null;
				
		// retrieve all POJO information from destination object
		Map<String, POJOInfo> infoMap = POJOUtil.getPOJOInfo(classRef);
		for (String current : infoMap.keySet())
		{
			POJOInfo info = infoMap.get(current);
			Object value = entries.get(current);
			
			if (value != null && IBeanObject.class.isAssignableFrom(value.getClass()))
			{
				// create a new instance of the java bean
				try
				{
					bean = info.getType().newInstance();
					((IBeanObject)value).populate(bean);
				} catch (Exception e)
				{
					continue;
				}
				callPOJOSetter(destination, info.getSetter(), bean);
			}
			else
			{
				// check whether the value type match with setter parameter
				Class<?> paramType = info.getType();
				if (value != null && !paramType.equals(value.getClass()))
					value = TypeConverter.convert(paramType, value);
				callPOJOSetter(destination, info.getSetter(), value);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static Object extractValue( Object source )
	{
		Object value = source;
		
		if ( value != null && BeanObject.isCollection( value.getClass() ) )
			value = new BeanCollection( (Collection<Object>)value );
		else
		if (value != null && !BeanObject.isPrimitive( value.getClass() ) )
			value = new BeanObject(value);
		else
			value = (value == null) ? null : value.toString();

		return value;
	}

	
}
