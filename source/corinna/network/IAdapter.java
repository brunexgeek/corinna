package corinna.network;

import org.jboss.netty.channel.Channel;

import corinna.exception.AdapterException;


public interface IAdapter<R,P>
{

	public Class<?> CONSTRUCTOR_ARGS[] = { IAdapterConfig.class };

	public boolean isCompatibleWith( R request, P response );
	
	public RequestEvent<?,?> translate( R request, P response, Channel channel ) 
		throws AdapterException;;

	public Class<?> getResponseType();

	public Class<?> getRequestType();
	
	public String getName();

}
