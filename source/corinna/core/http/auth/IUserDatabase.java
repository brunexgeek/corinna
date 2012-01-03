package corinna.core.http.auth;


public interface IUserDatabase
{
	
	public IUser getUser( String userName );
	
	public String[] getUserNames();
	
}
