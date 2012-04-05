package corinna.network;


public interface IAdapterFilter
{

	public boolean evaluate( Object request, Object response );
	
}
