package corinna.http.core.auth;


import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;


public class MessageDigest
{

	private java.security.MessageDigest digest;

	private byte[] hashValue = null;

	public MessageDigest( String arlgorithm ) throws NoSuchAlgorithmException
	{
		digest = java.security.MessageDigest.getInstance(arlgorithm);
	}
	
	public byte[] getHashValue()
	{
		if (hashValue == null)
		{
			hashValue = digest.digest();
			digest.reset();
		}
		return hashValue;
	}

	public String getHashString()
	{
		if (hashValue == null)
		{
			hashValue = digest.digest();
			digest.reset();
		}
		
		StringBuilder sb = new StringBuilder();
		for (byte value : hashValue)
			sb.append(String.format("%02x", value));

		return sb.toString();
	}

	public void update( int value )
	{
		byte[] array = new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
				(byte) (value >>> 8), (byte) value };

		digest.update(array);
	}

	public void update( long value )
	{
		byte[] array = new byte[] { (byte) (value >>> 56), (byte) (value >>> 48),
				(byte) (value >>> 40), (byte) (value >>> 32), (byte) (value >>> 24),
				(byte) (value >>> 16), (byte) (value >>> 8), (byte) value };

		digest.update(array);
	}

	public void update( byte[] input )
	{
		hashValue = null;
		digest.update(input);
	}

	public void update( String text, Charset charset )
	{
		hashValue = null;
		digest.update(text.getBytes(charset));
	}

	@Override
	public String toString()
	{
		return getHashString();
	}

}
