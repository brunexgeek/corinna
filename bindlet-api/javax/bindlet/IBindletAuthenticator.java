package javax.bindlet;

import javax.bindlet.IBindletRequest;
import javax.bindlet.IBindletResponse;


public interface IBindletAuthenticator
{

	public boolean authenticate( IBindletRequest request, IBindletResponse response );
	
	public void unauthorize( IBindletRequest request, IBindletResponse response );
	
	public Class<?> getRequestType();
	
	public Class<?> getResponseType();
		
}