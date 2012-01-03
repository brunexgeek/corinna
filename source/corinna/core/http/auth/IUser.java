package corinna.core.http.auth;


public interface IUser
{

	public abstract String getUserName();

	public abstract String getRealm();

	public abstract String getPassword();

}
