package corinna.network;

import org.jboss.netty.channel.Channel;

import corinna.exception.AdapterException;


public interface IAdapter
{

	public Class<?> CONSTRUCTOR_ARGS[] = { IAdapterConfig.class };

	/**
	 * Evaluates a request and response objects to check if them are compatible with the adapter.
	 * The compatibility referes to the ability of the adapter to converting the specified objects.
	 * 
	 * Optionally, this method can invoke one or more adapter filters to evaluate additional
	 * rules.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public boolean evaluate( Object request, Object response );
	
	public RequestEvent<?,?> translate( Object request, Object response, Channel channel ) 
		throws AdapterException;

	/**
	 * Returns the output response type generated by this adapter.
	 * 
	 * @return
	 */
	public Class<?> getOutputResponseType();

	/**
	 * Returns the output request type generated by this adapter.
	 * 
	 * @return
	 */
	public Class<?> getOutputRequestType();
	
	/**
	 * Returns the input request type compatible with this adapter.
	 * 
	 * @return
	 */
	public Class<?> getInputRequestType();
	
	/**
	 * Returns the input response type compatible with this adapter.
	 * 
	 * @return
	 */
	public Class<?> getInputResponseType();
	
	public String getName();

	public void onError( RequestEvent<?,?> event, Channel channel, Throwable exception );
	
	public void onSuccess( RequestEvent<?,?> event, Channel channel );
	
	public void addFilter( IAdapterFilter filter );
	
	public void removeFilter( IAdapterFilter filter );
	
}
