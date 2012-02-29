package corinna.http.core.auth;


import corinna.exception.ParseException;
import corinna.util.KeyValueParser;
import corinna.util.KeyValueParserConfig;


public class AuthorizationRequest extends DigestMessage
{

	protected static final String METHOD = "Digest";

	/**
	 * A string of 32 hex digits computed as defined below, which proves that the user knows a
	 * password
	 */
	protected static final String RESPONSE = "response";

	/**
	 * The user's name in the specified realm.
	 */
	protected static final String USERNAME = "username";

	/**
	 * The URI from Request-URI of the Request-Line; duplicated here because proxies are allowed to
	 * change the Request-Line in transit.
	 */
	protected static final String DIGEST_URI = "uri";

	/**
	 * This MUST be specified if a qop directive is sent (see above), and MUST NOT be specified if
	 * the server did not send a qop directive in the WWW-Authenticate header field. The
	 * cnonce-value is an opaque quoted string value provided by the client and used by both client
	 * and server to avoid chosen plaintext attacks, to provide mutual authentication, and to
	 * provide some message integrity protection. See the descriptions below of the calculation of
	 * the response- digest and request-digest values.
	 */
	protected static final String CNONCE = "cnonce";

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
	protected static final String NONCE_COUNT = "nc";

	private static KeyValueParserConfig config = null;

	static
	{
		config = new KeyValueParserConfig();
		config.setKeyValueSeparator("=");
		config.setPairSeparator(",");
		config.setQuotedValues(true);
	}

	public AuthorizationRequest( String data ) throws ParseException
	{
		super("dummy", "dummy");
		
		if (data == null || data.isEmpty())
			throw new NullPointerException("The data can not be null or empty");

		if (!data.startsWith(METHOD)) throw new ParseException("Invalid input data");

		//fields = new HashMap<String, String>();
		KeyValueParser.parse(data.substring(METHOD.length()), fields, config);
		checkField(RESPONSE);
		checkField(USERNAME);
	}

	private void checkField( String name ) throws ParseException
	{
		if (!contains(name))
			throw new ParseException("Missing required field + '" + name + "'");
	}

	public String getUserName()
	{
		return get(USERNAME);
	}

	public String getResponse()
	{
		return get(RESPONSE);
	}

	public String getDigestUri()
	{
		return get(DIGEST_URI);
	}

	public String getClientNonce()
	{
		return get(CNONCE);
	}

	public String getNonceCount()
	{
		return get(NONCE_COUNT);
	}

}
