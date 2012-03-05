package corinna.auth.login;


public interface ILoginContextListener
{

	public String getName();
	
	public void onSuccess();
	
	public void onFailure();
	
}
