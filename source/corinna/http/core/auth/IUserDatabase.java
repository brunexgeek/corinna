package corinna.http.core.auth;


public interface IUserDatabase
{
	
	public IUser getUser( String userName );
	
	public String[] getUserNames();
	
}
