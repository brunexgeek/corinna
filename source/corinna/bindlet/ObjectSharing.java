package corinna.bindlet;


import java.util.HashMap;
import java.util.Map;

import javax.bindlet.IObjectSharing;

import corinna.thread.ObjectLocker;


public class ObjectSharing implements IObjectSharing
{

	private Map<String, Object> shared;

	private ObjectLocker sharedLock;

	public ObjectSharing()
	{
		this.shared = new HashMap<String, Object>();
		this.sharedLock = new ObjectLocker();
	}

	@Override
	public Object getSharedObject( String name )
	{
		if (name == null) return null;

		sharedLock.readLock();
		try
		{
			return shared.get(name);
		} finally
		{
			sharedLock.readUnlock();
		}
	}

	@Override
	public String[] getSharedObjectNames()
	{
		sharedLock.readLock();
		try
		{
			if (shared.isEmpty()) return null;
			return (String[]) shared.keySet().toArray();
		} finally
		{
			sharedLock.readUnlock();
		}
	}

	@Override
	public void setSharedObject( String name, Object value )
	{
		if (name == null) throw new NullPointerException("The shared object name can not be null");

		sharedLock.writeLock();
		try
		{
			shared.put(name, value);
		} finally
		{
			sharedLock.writeUnlock();
		}
	}

	@Override
	public Object removeSharedObject( String name )
	{
		if (name == null) return null;

		sharedLock.writeLock();
		try
		{
			return shared.remove(name);
		} finally
		{
			sharedLock.writeUnlock();
		}
	}

}
