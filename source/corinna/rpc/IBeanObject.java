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


import java.util.Iterator;

import corinna.exception.BeanObjectException;


public interface IBeanObject extends IBeanType
{

	/**
	 * Get the value object associated with a key.
	 *
	 * @param key   A key string.
	 * @return      The object associated with the key.
	 * @throws      BeanObjectException if the key is not found.
	 */
	public Object get( String key ) throws BeanObjectException;

	/**
	 * Get the boolean value associated with a key.
	 *
	 * @param key   A key string.
	 * @return      The truth.
	 * @throws      BeanObjectException
	 *  if the value is not a Boolean or the String "true" or "false".
	 */
	public boolean getBoolean( String key ) throws BeanObjectException;

	/**
	 * Get the double value associated with a key.
	 * @param key   A key string.
	 * @return      The numeric value.
	 * @throws BeanObjectException if the key is not found or
	 *  if the value is not a Number object and cannot be converted to a number.
	 */
	public double getDouble( String key ) throws BeanObjectException;
	
	public float getFloat( String key ) throws BeanObjectException;

	/**
	 * Get the int value associated with a key.
	 *
	 * @param key   A key string.
	 * @return      The integer value.
	 * @throws   BeanObjectException if the key is not found or if the value cannot
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
	 * @throws   BeanObjectException if the key is not found or if the value cannot
	 *  be converted to a long.
	 */
	public long getLong( String key ) throws BeanObjectException;

	/**
	 * Get the string associated with a key.
	 *
	 * @param key   A key string.
	 * @return      A string which is the value.
	 * @throws   BeanObjectException if there is no string value for the key.
	 */
	public String getString( String key ) throws BeanObjectException;

	/**
     * Get the BeanObject value associated with a key.
     *
     * @param key   A key string.
     * @return      A BeanObject which is the value.
     * @throws      BeanObjectException if the key is not found or
     *  if the value is not a JSONObject.
     */
    public IBeanObject getObject(String key) throws BeanObjectException;
	
	/**
	 * Get an enumeration of the keys of the BeanObject.
	 *
	 * @return An iterator of the keys.
	 */
	public Iterator<String> keys();
	
	public void populate( Object destination );
	
	public void extract(  Object source );

	public void setString( String key, String value );
	
	public void set( String key, Object value );

	public IBeanCollection getCollection( String key ) throws BeanObjectException;
	
}
