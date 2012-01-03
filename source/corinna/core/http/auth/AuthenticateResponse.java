package corinna.core.http.auth;

import java.util.HashMap;
import java.util.Map;

import javax.bindlet.http.IWebBindletRequest;

import org.jboss.netty.handler.codec.http.HttpHeaders;

import corinna.exception.ParseException;
import corinna.util.KeyValueParser;
import corinna.util.KeyValueParserConfig;


//TODO: many methods in common with 'AuthorizationRequest'
public class AuthenticateResponse
{
	
	protected static final String METHOD = "Digest";

	/**
	 * A string to be displayed to users so they know which username and password to use. This
	 * string should contain at least the name of the host performing the authentication and might
	 * additionally indicate the collection of users who might have access. An example might be
	 * "registered_users@gotham.news.com".
	 */
	private static final String REALM = "realm";

	/**
	 * A quoted, space-separated list of URIs, as specified in RFC XURI [7], that define the
	 * protection space. If a URI is an abs_path, it is relative to the canonical root URL (see
	 * section 1.2 above) of the server being accessed. An absoluteURI in this list may refer to a
	 * different server than the one being accessed. The client can use this list to determine the
	 * set of URIs for which the same authentication information may be sent: any URI that has a URI
	 * in this list as a prefix (after both have been made absolute) may be assumed to be in the
	 * same protection space. If this directive is omitted or its value is empty, the client should
	 * assume that the protection space consists of all URIs on the responding server.
	 */
	private static final String DOMAIN = "domain";

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
	 * A flag, indicating that the previous request from the client was rejected because the nonce
	 * value was stale. If stale is TRUE (case-insensitive), the client may wish to simply retry the
	 * request with a new encrypted response, without reprompting the user for a new username and
	 * password. The server should only set stale to TRUE if it receives a request for which the
	 * nonce is invalid but with a valid digest for that nonce (indicating that the client knows the
	 * correct username/password). If stale is FALSE, or anything other than TRUE, or the stale
	 * directive is not present, the username and/or password are invalid, and new values must be
	 * obtained.
	 */
	private static final String STALE = "stale";

	/**
	 * A string indicating a pair of algorithms used to produce the digest and a checksum. If this
	 * is not present it is assumed to be "MD5". If the algorithm is not understood, the challenge
	 * should be ignored (and a different one used, if there is more than one).
	 */
	private static final String ALGORITHM = "algorithm";

	/**
	 * This directive is optional, but is made so only for backward compatibility with RFC 2069 [6];
	 * it SHOULD be used by all implementations compliant with this version of the Digest scheme. If
	 * present, it is a quoted string of one or more tokens indicating the "quality of protection"
	 * values supported by the server. The value "auth" indicates authentication; the value
	 * "auth-int" indicates authentication with integrity protection; see the descriptions below for
	 * calculating the response directive value for the application of this choice. Unrecognized
	 * options MUST be ignored.
	 */
	private static final String QOP_OPTIONS = "qop";

	/**
	 * This directive allows for future extensions. Any unrecognized directive MUST be ignored.
	 */
	private static final String AUTH_PARAM = "auth-param";

	private static final KeyValueParserConfig config;
	
	private Map<String,String> fields = null;
	
	static
	{
		config = new KeyValueParserConfig();
		config.setPairSeparator(",");
		config.setKeyValueSeparator("=");
		config.setQuotedValues(true);
	}
	
	public AuthenticateResponse( String realm, String nonce )
	{
		if (realm == null || realm.isEmpty())
			throw new NullPointerException("The realm can not be null or empty");
		if (nonce == null || nonce.isEmpty())
			throw new NullPointerException("The nonce can not be null or empty");
		
		fields = new HashMap<String,String>();
		
		// set the initial parameters
		setRealm(realm);
		setNonce(nonce);
		setQopOptions("auth");
	}
	
	public AuthenticateResponse( String data ) throws ParseException
	{
		if (data == null || data.isEmpty())
			throw new NullPointerException("The data must can not be null or empty");

		if (!data.startsWith(METHOD))
			throw new ParseException("Invalid input data");
		
		fields = new HashMap<String,String>();
		KeyValueParser.parse(data.substring(METHOD.length()), fields, config);
		checkField(NONCE);
		checkField(REALM);
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
	
	public String getRealm()
	{
		return fields.get(REALM);
	}
	
	public String getNonce()
	{
		return fields.get(NONCE);
	}
	
	public String getQopOptions()
	{
		return fields.get(QOP_OPTIONS);
	}
	
	public boolean isStale()
	{
		String value = fields.get(STALE);
		return (value != null && value.equalsIgnoreCase("true"));
	}
	
	public String getDomain()
	{
		return fields.get(DOMAIN);
	}
	
	public void setRealm( String realm )
	{
		if (realm == null || realm.isEmpty()) fields.remove(REALM);
		fields.put(REALM, realm);
	}
	
	public void setNonce( String nonce )
	{
		if (nonce == null || nonce.isEmpty()) fields.remove(NONCE);
		fields.put(NONCE, nonce);
	}
	
	public void setQopOptions( String qop )
	{
		if (qop == null || qop.isEmpty()) fields.remove(QOP_OPTIONS);
		fields.put(QOP_OPTIONS, qop);
	}
	
	public void setDomain( String domain )
	{
		if (domain == null || domain.isEmpty()) fields.remove(DOMAIN);
		fields.put(DOMAIN, domain);
	}
	
	public void setStale( boolean stale )
	{
		if (!stale)
			fields.remove(STALE);
		else
			fields.put(STALE, "TRUE");
	}
	
}
