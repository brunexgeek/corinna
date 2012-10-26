package corinna.rpc;


import java.util.Iterator;

import com.sun.org.apache.bcel.internal.generic.CASTORE;

import corinna.exception.BeanObjectException;
import corinna.json.core.JSONException;
import corinna.json.core.JSONObject;


public interface IBeanObject
{

	/**
	 * Get the value object associated with a key.
	 *
	 * @param key   A key string.
	 * @return      The object associated with the key.
	 * @throws      JSONException if the key is not found.
	 */
	public Object get( String key ) throws BeanObjectException;

	/**
	 * Get the boolean value associated with a key.
	 *
	 * @param key   A key string.
	 * @return      The truth.
	 * @throws      JSONException
	 *  if the value is not a Boolean or the String "true" or "false".
	 */
	public boolean getBoolean( String key ) throws BeanObjectException;

	/**
	 * Get the double value associated with a key.
	 * @param key   A key string.
	 * @return      The numeric value.
	 * @throws JSONException if the key is not found or
	 *  if the value is not a Number object and cannot be converted to a number.
	 */
	public double getDouble( String key ) throws BeanObjectException;
	
	public float getFloat( String key ) throws BeanObjectException;

	/**
	 * Get the int value associated with a key.
	 *
	 * @param key   A key string.
	 * @return      The integer value.
	 * @throws   JSONException if the key is not found or if the value cannot
	 *  be converted to an integer.
	 */
	public int getInt( String key ) throws BeanObjectException;
	
	public short getShort( String key ) throws BeanObjectException;

	public byte getByte( String key ) throws BeanObjectException;
	
	/**
	 * Get the long value associated with a key.
	 *
	 * @param key   A key string.
	 * @return      The long value.
	 * @throws   JSONException if the key is not found or if the value cannot
	 *  be converted to a long.
	 */
	public long getLong( String key ) throws BeanObjectException;

	/**
	 * Get the string associated with a key.
	 *
	 * @param key   A key string.
	 * @return      A string which is the value.
	 * @throws   JSONException if there is no string value for the key.
	 */
	public String getString( String key ) throws BeanObjectException;

	/**
     * Get the JSONObject value associated with a key.
     *
     * @param key   A key string.
     * @return      A JSONObject which is the value.
     * @throws      JSONException if the key is not found or
     *  if the value is not a JSONObject.
     */
    public IBeanObject getObject(String key) throws BeanObjectException;
	
	/**
	 * Get an enumeration of the keys of the JSONObject.
	 *
	 * @return An iterator of the keys.
	 */
	public Iterator<String> keys();
	
	public void populate( Object destination );
	
	public void extract(  Object source );
	
}
