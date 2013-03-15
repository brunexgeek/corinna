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
package corinna.util;

import corinna.thread.ObjectLocker;


public class ObjectPool<T>
{
	
	private T[] elements;
	
	private int capacity;
	
	private int head = 0;
	
	private int tail = 0;
	
	private boolean full = false;
	
	private ObjectLocker lock;
	
	@SuppressWarnings("unchecked")
	public ObjectPool( int capacity )
	{
		if (capacity <= 0)
			throw new IllegalArgumentException("The capacity must be a value greater than 0");
		
		this.capacity = capacity;
		this.elements = (T[]) new Object[capacity];
		this.lock = new ObjectLocker();
	}

	public int getCapacity()
	{
		return capacity;
	}
		
	public T borrow()
	{
		lock.writeLock();
		try
		{
			// check if the pool is empty
			if (head == tail && !full) return null;
			
			// take the element from circular list
			T element = elements[head];
			elements[head++] = null;
			if (head >= capacity) head = 0;
			full = false;
			
			return element;
		} catch (Throwable e)
		{
			return null;
		} finally
		{
			lock.writeUnlock();
		}
	}
	
	public void back( T obj )
	{
		lock.writeLock();
		try
		{
			// check if the pool is full
			if (full) return;
			
			// recycle the object if it implements 'IRecycleable'
			if (obj instanceof IRecyclable) ((IRecyclable)obj).recycle();
			
			// put the element in circular list
			elements[tail++] = obj;
			if (tail >= capacity) tail = 0;
			full = head == tail;
		} catch (Throwable e)
		{
			// supress any error
		} finally
		{
			lock.writeUnlock();
		}
	}
	
	public boolean isEmpty()
	{
		return !full;
	}
	
	public boolean isFull()
	{
		return full;
		
	}
	
}