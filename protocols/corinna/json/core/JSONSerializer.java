package corinna.json.core;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;

import corinna.core.ISerializer;
import corinna.exception.SerializationException;
import corinna.rpc.POJOUtil;
import corinna.rpc.TypeConverter;


public class JSONSerializer implements ISerializer
{

	private static Class<?> primitiveTypes[] = { Boolean.class, Integer.class, String.class,
			Double.class, Float.class, Byte.class, Character.class, Short.class, Long.class };

	@Override
	public String serialize( Object obj ) throws SerializationException
	{
		return serialize("value", obj);
	}

	@Override
	public Object deserialize( String text, Class<?> type )
	{
		if (!isPrimitive(type))
			return deserializeJavaBean(text, type);
		else
			return deserializePrimitive(text, type);
	}
	
	private Object deserializePrimitive( String text, Class<?> type )
	{
		try
		{
			JSONObject json = new JSONObject(text);
			return getJSONValue(json, "value", type);
		} catch (JSONException e)
		{
			return null;
		}
	}
	
	private Object getJSONValue( JSONObject json, String key, Class<?> type )
	{
		try
		{
			if (type == Integer.class) return json.getInt("value");
			if (type == Long.class) return json.getLong("value");
			if (type == Boolean.class) return json.getBoolean("value");
			if (type == Double.class) return json.getDouble("value");
			if (type == Float.class) return (float) json.getDouble("value");
			if (type == String.class) return json.getString("value");
			if (type == Byte.class) return (byte) json.getInt("value");
			if (type == Short.class) return (short) json.getInt("value");

			return null;
		} catch (JSONException e)
		{
			return null;
		}
	}

	private Object createJavaBean( Class<?> type )
	{
		try
		{
			if (type == null) return null;
			return type.newInstance();
		} catch (Exception e)
		{
			return null;
		}
	}
	
	private Object deserializeJavaBean( String text, Class<?> type )
	{
		Object bean = null;
		
		try
		{
			JSONObject json = new JSONObject(text);
			Method methods[] = POJOUtil.getMethods(type);
			
			if (methods == null || methods.length == 0) return null;
			
			// create the java bean object
			bean = createJavaBean(type);
			
			Iterator it = json.keys();
			while (it.hasNext())
			{
				String key = (String)it.next();
				Method method = POJOUtil.getSetter(methods, key);
				if (method == null) continue;
				
				// get the field type and value
				Class<?> fieldType = method.getParameterTypes()[0];
				Object value = TypeConverter.convert(fieldType, (Object)json.get(key).toString());
				
				try
				{
					method.invoke(bean, value);
				} catch (Exception e)
				{
					return null;
				}
			}
		} catch (JSONException e)
		{
			return null;
		}

		return bean;
	}

	@Override
	public String serialize( String name, Object obj ) throws SerializationException
	{
		if (!isPrimitive(obj))
			return serializeJavaBean(obj);
		else
			return serializePrimitive(name, obj);
	}

	private boolean isPrimitive( Class<?> cls )
	{
		for (Class<?> classRef : primitiveTypes)
			if (classRef == cls) return true;
		return false;
	}

	private boolean isPrimitive( Object obj )
	{
		for (Class<?> classRef : primitiveTypes)
			if (classRef == obj.getClass()) return true;
		return false;
	}

	private String serializePrimitive( String name, Object obj ) throws SerializationException
	{
		try
		{
			JSONObject json = new JSONObject();
			json.put(name, obj);
			return json.toString(3);
		} catch (JSONException e)
		{
			throw new SerializationException(e.getMessage(), e.getCause());
		}
	}

	private String serializeJavaBean( Object bean ) throws SerializationException
	{
		try
		{
			JSONObject json = new JSONObject(bean);
			return json.toString(3);
		} catch (JSONException e)
		{
			throw new SerializationException(e.getMessage(), e.getCause());
		}
	}

}
