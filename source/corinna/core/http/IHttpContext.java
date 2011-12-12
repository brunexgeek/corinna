package corinna.core.http;

import javax.bindlet.IBindlet;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;

import corinna.core.IContext;


public interface IHttpContext extends IContext<IHttpBindletRequest,IHttpBindletResponse>
{

	public IBindlet<IHttpBindletRequest,IHttpBindletResponse> createHttpBindlet( String bindletMapping );
	
}
