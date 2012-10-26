package corinna.core;


import corinna.exception.SerializationException;


public interface ISerializer
{

	public abstract String serialize( Object obj ) throws SerializationException;

	String serialize( String name, Object obj ) throws SerializationException;

	Object deserialize( String text, Class<?> type );

}
