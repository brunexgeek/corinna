package corinna.http.core;


import java.io.IOException;
import java.util.Properties;

import corinna.util.ResourceLoader;


/**
 * Map file extensions to MIME types. Based on the Apache mime.types file.
 * http://www.iana.org/assignments/media-types/
 */
public class MimeTypeUtil
{

	public static final String MIME_APPLICATION_OCTET_STREAM = "application/octet-stream";
	
	private static Properties prop;

	static
	{
		prop = new Properties();
		try
		{
			prop.load(ResourceLoader.getResourceAsStream("corinna/http/core/MimeTypeUtil.properties"));
		} catch (IOException e)
		{
		}
	}
	
	private MimeTypeUtil( String mimeType, String extension )
	{
	}

	/**
	 * Returns the corresponding MIME type to the given extension. If no MIME type was found it
	 * returns the default value.
	 */
	public static String getMimeType( String ext, String defaultValue )
	{
		Object value = prop.get(ext);
		if (value == null)
			return defaultValue;
		else
			return value.toString();
	}

	/**
	 * Returns the corresponding MIME type to the given extension. If no MIME type was found it
	 * returns <code>null</code>.
	 */
	public static String getMimeType( String ext )
	{
		return getMimeType(ext, null);
	}
}
