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


import java.util.HashSet;


public final class TypeConverter
{

	private static final Class<?>[] TYPES_ARRAY = { boolean.class, byte.class, short.class,
			int.class, long.class, float.class, double.class, void.class, Boolean.class,
			Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class,
			Void.class, String.class };

	private static final HashSet<Class<?>> TYPES_SET = getWrapperTypes();

	private TypeConverter()
	{
	}

	public static boolean isSupportedType( Class<?> type )
	{
		boolean valid = TYPES_SET.contains(type);
		valid |= Enum.class.isAssignableFrom(type);
		return valid;
	}

	private static HashSet<Class<?>> getWrapperTypes()
	{
		HashSet<Class<?>> list = new HashSet<Class<?>>();
		for (Class<?> type : TYPES_ARRAY)
			list.add(type);
		return list;
	}

	public static long toLong( String value )
	{
		return Long.valueOf(value);
	}

	public static int toInteger( String value ) throws NumberFormatException
	{
		return Integer.valueOf(value);
	}

	public static float toFloat( String value ) throws NumberFormatException
	{
		return Float.valueOf(value);
	}

	public static double toDouble( String value ) throws NumberFormatException
	{
		return Double.valueOf(value);
	}

	public static boolean toBoolean( String value )
	{
		if (value == null || value.isEmpty()) return false;
		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes")) return true;
		try
		{
			int i = Integer.parseInt(value);
			if (i != 0) return true;
			return false;
		} catch (Exception e)
		{
			return false;
		}
	}

	public static byte toByte( String value ) throws NumberFormatException
	{
		return Byte.valueOf(value);
	}

	public static short toShort( String value ) throws NumberFormatException
	{
		return Short.valueOf(value);
	}

	public static Object toEnum( Class<Enum<?>> type, String value )
	{
		Enum<?>[] values = type.getEnumConstants();
		value = value.trim();
		
		for (Enum<?> current : values)
			if (current.toString().equals(value)) return current;
		return null;
	}

	
	@SuppressWarnings("unchecked")
	@Deprecated
	public static Object convert( Class<?> type, String value )
	{
		if (type.equals(Integer.class) || type.equals(int.class)) return toInteger(value);
		if (type.equals(Long.class) || type.equals(long.class)) return toLong(value);
		if (type.equals(Float.class) || type.equals(float.class)) return toFloat(value);
		if (type.equals(Double.class) || type.equals(double.class)) return toDouble(value);
		if (type.equals(Byte.class) || type.equals(byte.class)) return toByte(value);
		if (type.equals(Short.class) || type.equals(short.class)) return toShort(value);
		if (type.equals(Boolean.class) || type.equals(boolean.class)) return toBoolean(value);
		if (type.equals(String.class)) return value;
		if (Enum.class.isAssignableFrom(type)) return toEnum((Class<Enum<?>>)type, value);

		throw new ClassCastException("Unsupported type '" + type.getName() + "'");
	}

	public static Object toJavaBean( Class<?> type, BeanObject value )
	{
		Object object;

		try
		{
			object = type.newInstance();
		} catch (Exception e)
		{
			return null;
		}
		
		value.populate(object);
		return object;
	}
	
	@SuppressWarnings("unchecked")
	public static Object convert( Class<?> type, Object value )
	{
		if (value instanceof BeanObject)
			return toJavaBean(type, (BeanObject)value);
		
		String text = value.toString();
		
		if (type.equals(Integer.class) || type.equals(int.class)) return toInteger(text);
		if (type.equals(Long.class) || type.equals(long.class)) return toLong(text);
		if (type.equals(Float.class) || type.equals(float.class)) return toFloat(text);
		if (type.equals(Double.class) || type.equals(double.class)) return toDouble(text);
		if (type.equals(Byte.class) || type.equals(byte.class)) return toByte(text);
		if (type.equals(Short.class) || type.equals(short.class)) return toShort(text);
		if (type.equals(Boolean.class) || type.equals(boolean.class)) return toBoolean(text);
		if (type.equals(String.class)) return text;
		if (type.isEnum()) return toEnum((Class<Enum<?>>)type, text);

		throw new ClassCastException("Unsupported type '" + type.getName() + "'");
	}
	
	/**
	 * Check whether the specified class type represents a primitive value.
	 * 
	 * @param type Type to check.
	 * @return
	 */
	public static boolean isPrimitive( Class<?> type )
	{
		if (type == null) return false;
		
		boolean valid = TYPES_SET.contains(type);
		valid |= Enum.class.isAssignableFrom(type);
		return valid;
	}
		
	public static boolean isPrimitive( Object value )
	{
		if (value == null) return false;
		return isPrimitive(value.getClass());
	}
	
}
