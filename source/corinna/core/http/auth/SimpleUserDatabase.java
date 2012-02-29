package corinna.core.http.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import corinna.thread.ObjectLocker;
import corinna.util.ResourceLoader;


public class SimpleUserDatabase implements IUserDatabase
{

	private Map<String,User> users;
	
	private ObjectLocker usersLock;

	private String[] usersArray;
	
	private String fileName;
	
	public SimpleUserDatabase( String fileName ) throws IOException
	{
		if (fileName == null || fileName.isEmpty())
			throw new IllegalArgumentException("The file name can not be null");
		
		this.fileName = fileName;

		usersLock = new ObjectLocker();
		users = new HashMap<String,User>();
		
		loadUsers();
	}
	
	protected void loadUsers() throws IOException
	{
		InputStream is = null;
		
		try
		{
			is = ResourceLoader.getResourceAsStream(fileName);
			Properties prop = new Properties();
			prop.load(is);
			
			for (Map.Entry<Object, Object> entry : prop.entrySet() )
			{
				Object value = entry.getKey();
				if (value == null) continue;
				String[] values = value.toString().split("@");
				String userName = values[0];
				String realm = "";
				if (values.length > 1) realm = values[1];
				
				value = entry.getValue();
				if (value == null) continue;
				String password = value.toString();
				
				if (userName.isEmpty() || password.isEmpty()) continue;
				
				User user = new User(userName, realm);
				user.setPassword(password);
				users.put(userName, user);
			}
			
		} catch (IOException e)
		{
			if (is != null) is.close();
			throw e;
		}
		is.close();
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
		usersArray = null;
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
