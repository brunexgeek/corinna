package corinna.core.http.auth;


public class User implements IUser
{

	private String userName;
	
	private String realm = "";
	
	private String password = "";

	public User( String userName, String realm )
	{
		this.userName = userName;
		this.realm = realm;
	}
	
	public void setUserName( String userName )
	{
		if (userName == null || userName.isEmpty()) return;
		this.userName = userName;
	}

	@Override
	public String getUserName()
	{
		return userName;
	}

	public void setRealm( String realm )
	{
		if (realm == null) realm = "";
		this.realm = realm;
	}

	@Override
	public String getRealm()
	{
		return realm;
	}

	public void setPassword( String password )
	{
		if (password == null || password.isEmpty()) return;
		this.password = password;
	}

	@Override
	public String getPassword()
	{
		return password;
	} 
	
}
