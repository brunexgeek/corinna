package corinna.core;

import java.io.IOException;

import javax.bindlet.IBindletContext;

import corinna.exception.BindletException;
import corinna.network.RequestEvent;


public interface IContextRequestListener<R,P>
{


	public void contextRequestReceived( IBindletContext context,
		RequestEvent<R,P> event ) throws BindletException, IOException;
	
}
