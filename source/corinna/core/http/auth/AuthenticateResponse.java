package corinna.core.http.auth;

import corinna.exception.ParseException;
import corinna.util.KeyValueParser;
import corinna.util.KeyValueParserConfig;


public class AuthenticateResponse extends DigestMessage
{

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
	protected static final String DOMAIN = "domain";

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
	protected static final String STALE = "stale";

	private static final KeyValueParserConfig config;
	
	static
	{
		config = new KeyValueParserConfig();
		config.setPairSeparator(",");
		config.setKeyValueSeparator("=");
		config.setQuotedValues(true);
	}
	
	public AuthenticateResponse( String realm, String nonce )
	{
		super(realm, nonce);
		
		setQopOptions("auth");
	}
	
	public AuthenticateResponse( String data ) throws ParseException
	{
		super("dummy", "dummy");
		
		if (data == null || data.isEmpty())
			throw new NullPointerException("The data must can not be null or empty");

		if (!data.startsWith(METHOD))
			throw new ParseException("Invalid input data");
		
		//Map<String,String> values = new HashMap<String,String>();
		KeyValueParser.parse(data.substring(METHOD.length()), fields, config);
		checkField(NONCE);
		checkField(REALM);
	}

	private void checkField( String name ) throws ParseException
	{
		if (!contains(name))
			throw new ParseException("Missing required field + '" + name + "'");
	}
	
	public boolean isStale()
	{
		String value = get(STALE);
		return (value != null && value.equalsIgnoreCase("true"));
	}
	
	public String getDomain()
	{
		return get(DOMAIN);
	}
		
	public void setDomain( String domain )
	{
		if (domain == null || domain.isEmpty()) 
			remove(DOMAIN);
		else
			set(DOMAIN, domain);
	}
	
	public void setStale( boolean stale )
	{
		if (!stale)
			remove(STALE);
		else
			set(STALE, "TRUE");
	}
	
}
