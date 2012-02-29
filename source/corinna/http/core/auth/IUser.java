package corinna.http.core.auth;


public interface IUser
{

	public abstract String getUserName();

	public abstract String getRealm();

	public abstract String getPassword();

}
