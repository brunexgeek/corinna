package corinna.http.jsp;

import java.io.PrintWriter;

import javax.bindlet.http.IHttpBindletRequest;


// TODO: rename to "ServerPageRender"
public abstract class IServerPageRender
{
	
	public IHttpBindletRequest request = null;
	
	public abstract void render( PrintWriter out );
	
}
