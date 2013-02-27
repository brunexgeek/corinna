package corinna.http.core.auth.basic;

import javax.bindlet.IBindletAuthenticator;
import javax.bindlet.IBindletRequest;
import javax.bindlet.IBindletResponse;
import javax.bindlet.http.HttpStatus;
import javax.bindlet.http.IWebBindletRequest;
import javax.bindlet.http.IWebBindletResponse;

import org.jboss.netty.handler.codec.http.HttpHeaders;

import corinna.http.core.auth.IUser;
import corinna.http.core.auth.IUserDatabase;
import corinna.util.Base64;


public class BasicBindletAuthenticator implements IBindletAuthenticator
{

	private static final String METHOD = "Basic";
	
	private IUserDatabase database;
	
	public BasicBindletAuthenticator( IUserDatabase database )
	{
		if (database == null)
			throw new NullPointerException("The user database object can not be null");

		this.database = database;
	}
	
	@Override
	public boolean authenticate( IBindletRequest request, IBindletResponse response )
	{
		if (!(request instanceof IWebBindletRequest)) return false;
		if (!(response instanceof IWebBindletResponse)) return false;
		
		IWebBindletRequest req = (IWebBindletRequest) request;
		
		String value = req.getHeader(HttpHeaders.Names.AUTHORIZATION);
		if (value != null && authenticate(req)) return true;
	
		unauthorize(request, response);
		return false;
	}

	@Override
	public Class<?> getRequestType()
	{
		return IWebBindletRequest.class;
	}

	@Override
	public Class<?> getResponseType()
	{
		return IWebBindletResponse.class;
	}

	protected boolean authenticate( IWebBindletRequest request )
	{
		String data = request.getHeader(HttpHeaders.Names.AUTHORIZATION);

		if (!data.startsWith(METHOD))
			return false;//throw new ParseException("Invalid method");
		
		String[] parts = data.split(" ");
		if (parts.length  != 2)
			return false;//throw new ParseException("Invalid input data");
		
		String value = Base64.decodeString(parts[1]);
		parts = value.split(":");
		if (parts.length < 2)
			return false;//throw new ParseException("Invalid username/password data");
		
		String userName = parts[0].trim();
		String password = parts[1].trim();
		
		IUser user = database.getUser(userName);
		if (user == null) return false;
		boolean result =  user.getPassword().equals(password);
		if (result) request.setUserName(userName);
		return result;
	}

	@Override
	public void unauthorize( IBindletRequest request, IBindletResponse response )
	{
		if (!(response instanceof IWebBindletResponse)) return;
		IWebBindletResponse res = (IWebBindletResponse) response;
		
		res.setHeader(HttpHeaders.Names.WWW_AUTHENTICATE, "Basic realm=\"vaas.tts\"");
		res.setStatus(HttpStatus.UNAUTHORIZED);
	}
	
}
