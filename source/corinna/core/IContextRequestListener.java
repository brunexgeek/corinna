package corinna.core;

import java.io.IOException;

import javax.bindlet.IBindletContext;
import javax.bindlet.IBindletRequest;
import javax.bindlet.IBindletResponse;
import javax.bindlet.exception.BindletException;

import corinna.network.RequestEvent;


public interface IContextRequestListener<R extends IBindletRequest,P extends IBindletResponse>
{


	public void contextRequestReceived( IBindletContext context,
		RequestEvent<R,P> event ) throws BindletException, IOException;
	
}
