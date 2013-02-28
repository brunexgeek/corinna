package corinna.util;

import corinna.thread.ObjectLocker;


public class ThreadSafeValue<T extends Object>
{

	protected ObjectLocker lock = new ObjectLocker();
	
	protected T value;
	
	public ThreadSafeValue( T value )
	{
		this.value = value;
	}
	
	public void set( T value )
	{
		lock.writeLock();
		this.value = value;
		lock.writeUnlock();
	}
	
	public T get( )
	{
		lock.readLock();
		T result = this.value;
		lock.readUnlock();
		
		return result;
	}
	
}
