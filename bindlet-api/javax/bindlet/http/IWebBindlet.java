package javax.bindlet.http;

//TODO: mover para 'javax.bindlet.http'
public interface IWebBindlet
{
	
	/**
	 * Returns true if the specified request can be processed by this bindlet
	 * 
	 * @param request
	 * @return
	 */
	public boolean canProcess( IWebBindletRequest request );
	
}
