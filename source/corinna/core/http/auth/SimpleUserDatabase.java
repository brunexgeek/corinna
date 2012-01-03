package corinna.core.http.auth;

import java.util.HashMap;
import java.util.Map;

import corinna.thread.ObjectLocker;


public class SimpleUserDatabase implements IUserDatabase
{

	private Map<String,User> users;
	
	private ObjectLocker usersLock;

	private String[] usersArray;
	
	public SimpleUserDatabase( String fileName )
	{
		usersLock = new ObjectLocker();
		users = new HashMap<String,User>();
	}
	
	@Override
	public IUser getUser( String userName )
	{
		if (userName == null) return null;
		
		usersLock.readLock();
		IUser user = users.get(userName);
		usersLock.readUnlock();
		
		return user;
	}

	public void addUser( User user )
	{
		if (user == null) return;
		
		usersLock.writeLock();
		users.put(user.getUserName(), user);
		usersLock.writeUnlock();
	}

	@Override
	public String[] getUserNames()
	{
		String[] output = null;
		
		usersLock.readLock();
		if (usersArray != null)
		{
			usersLock.readUnlock();
			usersLock.writeLock();
			output = usersArray = users.keySet().toArray(new String[0]);
			usersLock.writeUnlock();
		}
		else
		{
			output = usersArray;
			usersLock.readUnlock();
		}
		
		return output;	
	}

}
