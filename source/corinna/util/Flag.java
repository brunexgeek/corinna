package corinna.util;

import corinna.thread.ObjectLocker;


public class Flag
{

	private boolean flag = false;

	private ObjectLocker lock;
	
	public Flag()
	{
		lock = new ObjectLocker();
	}
	
	public Flag( boolean value )
	{
		this();
		flag = value;
	}
	
	public void setFlag( boolean flag )
	{
		lock.writeLock();
		this.flag = flag;
		lock.writeUnlock();
	}

	public boolean setFlag( boolean older, boolean newer )
	{
		boolean result;
		
		lock.writeLock();
		if (this.flag == older) 
		{
			this.flag = newer;
			result = true;
		}
		else
			result = false; 
		lock.writeUnlock();
		return result;
	}
	
	public boolean isFlag()
	{
		lock.readLock();
		boolean result = flag;
		lock.readUnlock();
		return result;
	}
	
	
	
	
}
