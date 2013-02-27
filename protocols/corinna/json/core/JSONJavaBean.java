package corinna.json.core;

import java.util.Iterator;

import corinna.rpc.BeanObject;


/**
 * Provide a set of methods to convertion between {@link BeanObject} and {@link JSONObject}.
 * 
 * @author Bruno Ribeiro
 * @version 2.1
 * @since 2.1
 */
public class JSONJavaBean
{

	protected JSONJavaBean()
	{
	}
	
	/**
	 * Create an {@link BeanObject} from an {@link JSONObject}. The JSONObject can only have primitive
	 * or JSONObject type fields. 
	 * 
	 * @param obj
	 * @return
	 * @throws JSONException
	 */
	public static BeanObject toJavaBean( JSONObject obj ) throws JSONException
	{
		BeanObject bean = new BeanObject();
		
		Iterator<String> it = obj.keys();
		while (it.hasNext())
		{
			String key = it.next();
			Object value = obj.opt(key);
			
			if (value instanceof JSONObject)
				value = toJavaBean( (JSONObject) value);
			else
			if (!BeanObject.isPrimitive(value.getClass()))
				throw new JSONException("Invalid field type '" + value.getClass().getCanonicalName() + "'");
			
			bean.set(key, value);
		}
		
		return bean;
	}
	
	/**
	 * Create an {@link JSONObject} from an {@link BeanObject}. The BeanObject can only have primitive
	 * or BeanObject type fields. 
	 * 
	 * @param obj
	 * @return
	 * @throws JSONException
	 */
	public static JSONObject fromJavaBean( BeanObject obj ) throws JSONException
	{
		JSONObject json = new JSONObject();
		
		Iterator<String> it = obj.keys();
		while (it.hasNext())
		{
			String key = it.next();
			Object value = obj.get(key);
			
			if (value instanceof BeanObject)
				value = fromJavaBean( (BeanObject) value);
			else
			if (!BeanObject.isPrimitive(value.getClass()))
				throw new JSONException("Invalid field type '" + value.getClass().getCanonicalName() + "'");
			
			json.put(key, value);
		}
		
		return json;
	}
	
	
}
