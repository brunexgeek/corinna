package corinna.http.core;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.bindlet.http.IWebBindletRequest;


public class HttpUtils
{

	private static final String RFC1123_PATTERN = "EEE, dd MMM yyyyy HH:mm:ss z";

	private final static SimpleDateFormat DATE_FORMAT;

	static
	{
		DATE_FORMAT = new SimpleDateFormat(RFC1123_PATTERN, Locale.ENGLISH);
		DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	
	/**
	 * Parse a query string. The given query string must be already URL decoded.
	 * 
	 * @param list
	 * @param data
	 * @param pairSeparator
	 * @param keyValueSeparator
	 */
	// TODO: parse the parameter values as String array of separated values (for multi-values parameter)
	public static void parseQueryString( Map<String, String> list, String data,
		String pairSeparator, String keyValueSeparator )
	{
		if (data == null) return;

		int start = 0;
		int end = data.indexOf(pairSeparator, start);
		if (end < 0) end = data.length();

		while (start >= 0)
		{
			// don't process empty strings
			if (start != end)
			{
				// find the position of key-value separator
				int pos = data.indexOf(keyValueSeparator, start);
				if (pos == -1) pos = end;
				// extract the key and value
				String key = data.substring(start, pos);
				String value = "";
				if (pos < end) value = data.substring(pos + 1, end);
				list.put(key, value);
			}
			// find the next pair
			start = data.indexOf(pairSeparator, start);
			if (start < 0) break;
			start++;
			end = data.indexOf(pairSeparator, start);
			if (end < 0) end = data.length();
		}
	}

	public static StringBuffer getRequestURL( IWebBindletRequest req )
	{
		StringBuffer url = new StringBuffer();
		String scheme = req.getScheme();
		int port = req.getServerPort();

		url.append(scheme);
		url.append("://");
		url.append(req.getServerName());
		if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443))
		{
			url.append(':');
			url.append(req.getServerPort());
		}
		url.append(req.getContextPath());
		url.append(req.getBindletPath());
		url.append(req.getResourcePath());
		return url;
	}

	public static StringBuffer getRequestURI( IWebBindletRequest req )
	{
		StringBuffer uri = getRequestURL(req);
		String query = req.getQueryString();
		if (query != null && !query.isEmpty())
		{
			uri.append("?");
			uri.append(query);
		}
		return uri;
	}

	public static StringBuffer getRequestURN( IWebBindletRequest req )
	{
		StringBuffer urn = new StringBuffer();
		urn.append(req.getContextPath());
		urn.append(req.getBindletPath());
		urn.append(req.getResourcePath());
		return urn;
	}

	/**
	 * Format a date as specified in RFC 1123.
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate( Date date )
	{
		return DATE_FORMAT.format(date);
	}

	public static String formatDate( long date )
	{
		return DATE_FORMAT.format(date);
	}

	public static long parseDateToLong( String date )
	{
		try
		{
			return DATE_FORMAT.parse(date).getTime();
		} catch (ParseException e)
		{
			return -1;
		}
	}

	public static Date parseDateToDate( String date )
	{
		try
		{
			return DATE_FORMAT.parse(date);
		} catch (ParseException e)
		{
			return null;
		}
	}
	
	public static String matchURI( String pattern, String uri )
	{
		if (uri == null || uri.isEmpty())
			throw new IllegalArgumentException("The uri can not be null or empty");

		// handle the root pattern
		if (pattern == null || pattern.isEmpty() || pattern.equals("/")) return "";

		// ensures the first slash
		if (pattern.charAt(0) != '/') pattern = "/" + pattern;
		if (uri.charAt(0) != '/') uri = "/" + uri;
		// ensures the last character in pattern is not a slash
		if (pattern.charAt(pattern.length() - 1) == '/') pattern = pattern.substring(0, pattern.length() - 1);
		
		// indexes
		int pi = 0;
		int ui = 0;
		// lengths
		int pl = pattern.length();
		int ul = uri.length();
		
		char pc = '\0', uc = '\0';
		
		while (pi < pl && ui < ul)
		{
			pc = pattern.charAt(pi);
			uc = uri.charAt(ui);
			
			// handle the asterisk operator
			if (pc == '*' && uc != '/')
				ui++;
			else
			if (pc == '*')
				pi++;
			else
			// handle the regular character
			if (uc != pc)
				return null;
			else
			{
				ui++;
				pi++;
			}
		}
		
		// if not reached the end of pattern AND the last pattern chaarcter is not a asterisk
		if (pi < pl && pc != '*') return null;
		// check if the last character founded (or the next) is a slash
		if (uri.charAt(ui-1) != '/' && (ui < ul && uri.charAt(ui) != '/') ) return null;
		
		return uri.substring(0, ui);
	}

	public static String clearUri( String uri )
	{
		if (uri == null) return null;
		
		StringBuffer sb = new StringBuffer(uri.length());
		
		// remove all repeated slashes of URI
		int c = 0;
		boolean slash = false;
		boolean end = false;
		while (c < uri.length())
		{
			char current = uri.charAt(c++);
			end = (current == '?');
			
			if (!end && current == '/')
			{
				if (slash) continue;
				slash = true;
			}
			else
				slash = false;
			sb.append(current);
		}
		return sb.toString();
	}
	
}
