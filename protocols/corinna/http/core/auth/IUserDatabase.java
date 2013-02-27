package corinna.http.core.auth;


// TODO: renomear para 'AuthenticationHandler' e incluir o método 'authenticate' que recebe o nome de usuário e senha e retorna um IUser
public interface IUserDatabase
{
	
	public IUser getUser( String userName );
	
	public String[] getUserNames();
	
}
