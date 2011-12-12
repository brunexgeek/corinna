package corinna.core.http;


import java.util.Collection;

import corinna.core.IBindletRegistration;


public interface IHttpBindletRegistration extends IBindletRegistration
{

	public void addMapping( String... urlPatterns );

	public Collection<String> getMappings();

	/**
	 * Gets the names of the methods supported by the underlying bindlet.
	 * 
	 * <p>
	 * This is the same set of methods included in the <code>Allow</code> response header in
	 * response to an <code>OPTIONS</code> request method processed by the underlying bindlet.
	 * </p>
	 * 
	 * @return array of names of the methods supported by the underlying bindlet
	 */
	public String[] getBindletMethods();

}
