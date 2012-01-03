package corinna.core.http.auth;

import java.util.HashMap;
import java.util.Map;

import corinna.exception.ParseException;
import corinna.util.KeyValueParser;
import corinna.util.KeyValueParserConfig;

// TODO: many methods in common with 'AuthenticateResponse'
public class AuthorizationRequest
{

	protected static final String METHOD = "Digest";
	
	/**
	 * A string of 32 hex digits computed as defined below, which proves that the user knows a
	 * password
	 */
	private static final String RESPONSE = "response";

	/**
	 * The user's name in the specified realm.
	 */
	private static final String USERNAME = "username";

	/**
	 * The URI from Request-URI of the Request-Line; duplicated here because proxies are allowed to
	 * change the Request-Line in transit.
	 */
	private static final String DIGEST_URI = "digest-uri";

	/**
	 * Indicates what "quality of protection" the client has applied to the message. If present, its
	 * value MUST be one of the alternatives the server indicated it supports in the
	 * WWW-Authenticate header. These values affect the computation of the request-digest. Note that
	 * this is a single token, not a quoted list of alternatives as in WWW- Authenticate. This
	 * directive is optional in order to preserve backward compatibility with a minimal
	 * implementation of RFC 2069 [6], but SHOULD be used if the server indicated that qop is
	 * supported by providing a qop directive in the WWW-Authenticate header field.
	 */
	private static final String QOP = "qop";

	/**
	 * This MUST be specified if a qop directive is sent (see above), and MUST NOT be specified if
	 * the server did not send a qop directive in the WWW-Authenticate header field. The
	 * cnonce-value is an opaque quoted string value provided by the client and used by both client
	 * and server to avoid chosen plaintext attacks, to provide mutual authentication, and to
	 * provide some message integrity protection. See the descriptions below of the calculation of
	 * the response- digest and request-digest values.
	 */
	private static final String CNONCE = "cnonce";

	/**
	 * This MUST be specified if a qop directive is sent (see above), and MUST NOT be specified if
	 * the server did not send a qop directive in the WWW-Authenticate header field. The nc-value is
	 * the hexadecimal count of the number of requests (including the current request) that the
	 * client has sent with the nonce value in this request. For example, in the first request sent
	 * in response to a given nonce value, the client sends "nc=00000001". The purpose of this
	 * directive is to allow the server to detect request replays by maintaining its own copy of
	 * this count - if the same nc-value is seen twice, then the request is a replay. See the
	 * description below of the construction of the request-digest value.
	 */
	private static final String NONCE_COUNT = "nc";

	/**
	 * This directive allows for future extensions. Any unrecognized directive MUST be ignored.
	 */
	private static final String AUTH_PARAM = "auth-param";
	
	/**
	 * A server-specified data string which should be uniquely generated each time a 401 response is
	 * made. It is recommended that this string be base64 or hexadecimal data. Specifically, since
	 * the string is passed in the header lines as a quoted string, the double-quote character is
	 * not allowed.
	 */
	private static final String NONCE = "nonce";

	/**
	 * A string of data, specified by the server, which should be returned by the client unchanged
	 * in the Authorization header of subsequent requests with URIs in the same protection space. It
	 * is recommended that this string be base64 or hexadecimal data.
	 */
	private static final String OPAQUE = "opaque";
	
	/**
	 * A string to be displayed to users so they know which username and password to use. This
	 * string should contain at least the name of the host performing the authentication and might
	 * additionally indicate the collection of users who might have access. An example might be
	 * "registered_users@gotham.news.com".
	 */
	private static final String REALM = "realm";
	
	private static final String URI = "uri";
	
	private static KeyValueParserConfig config = null;
	
	private Map<String,String> fields = null;
	
	static
	{
		config = new KeyValueParserConfig();
		config.setKeyValueSeparator("=");
		config.setPairSeparator(",");
		config.setQuotedValues(true);
	}
	
	public AuthorizationRequest( String data ) throws ParseException
	{
		if (data == null || data.isEmpty())
			throw new NullPointerException("The data must can not be null or empty");

		if (!data.startsWith(METHOD))
			throw new ParseException("Invalid input data");
		
		fields = new HashMap<String,String>();
		KeyValueParser.parse(data.substring(METHOD.length()), fields, config);
		checkField(RESPONSE);
		checkField(USERNAME);
	}


	private void checkField( String name ) throws ParseException
	{
		if (!fields.containsKey(name))
			throw new ParseException("Missing required field + '" + name + "'");
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		
		sb.append(METHOD);
		sb.append(" ");
		
		for (Map.Entry<String, String> entry : fields.entrySet())
			first = write(sb, entry.getKey(), entry.getValue(), first);
		
		return sb.toString();
	}
	
	protected boolean write( StringBuffer sb, String name, String value, boolean first )
	{
		if (!first) sb.append(", ");
		sb.append(name);
		sb.append("=\"");
		sb.append(value);
		sb.append("\"");
		
		return false;
	}
	
	public String getUserName()
	{
		return fields.get(USERNAME);
	}
	
	public String getResponse()
	{
		return fields.get(RESPONSE);
	}
	
	public String getNonce()
	{
		return fields.get(NONCE);
	}
	
	public String getRealm()
	{
		return fields.get(REALM);
	}


	public String getUri()
	{
		return fields.get(URI);
	}

	public String getCNonce()
	{
		return fields.get(CNONCE);
	}


	public String getNonceCount()
	{
		return fields.get(NONCE_COUNT);
	}


	public String getQopOptions()
	{
		return fields.get(QOP);
	}
	
}
