package corinna.http.core.auth;


import java.util.HashMap;
import java.util.Map;


public abstract class DigestMessage
{

	protected static final String METHOD = "Digest";

	/**
	 * A string to be displayed to users so they know which username and password to use. This
	 * string should contain at least the name of the host performing the authentication and might
	 * additionally indicate the collection of users who might have access. An example might be
	 * "registered_users@gotham.news.com".
	 */
	protected static final String REALM = "realm";

	/**
	 * A server-specified data string which should be uniquely generated each time a 401 response is
	 * made. It is recommended that this string be base64 or hexadecimal data. Specifically, since
	 * the string is passed in the header lines as a quoted string, the double-quote character is
	 * not allowed.
	 */
	protected static final String NONCE = "nonce";

	/**
	 * A string of data, specified by the server, which should be returned by the client unchanged
	 * in the Authorization header of subsequent requests with URIs in the same protection space. It
	 * is recommended that this string be base64 or hexadecimal data.
	 */
	protected static final String OPAQUE = "opaque";

	/**
	 * A string indicating a pair of algorithms used to produce the digest and a checksum. If this
	 * is not present it is assumed to be "MD5". If the algorithm is not understood, the challenge
	 * should be ignored (and a different one used, if there is more than one).
	 */
	protected static final String ALGORITHM = "algorithm";

	/**
	 * This directive is optional, but is made so only for backward compatibility with RFC 2069 [6];
	 * it SHOULD be used by all implementations compliant with this version of the Digest scheme. If
	 * present, it is a quoted string of one or more tokens indicating the "quality of protection"
	 * values supported by the server. The value "auth" indicates authentication; the value
	 * "auth-int" indicates authentication with integrity protection; see the descriptions below for
	 * calculating the response directive value for the application of this choice. Unrecognized
	 * options MUST be ignored.
	 */
	protected static final String QOP_OPTIONS = "qop";

	/**
	 * This directive allows for future extensions. Any unrecognized directive MUST be ignored.
	 */
	protected static final String AUTH_PARAM = "auth-param";

	protected Map<String, String> fields = null;

	public DigestMessage( String realm, String nonce )
	{
		if (realm == null || realm.isEmpty())
			throw new NullPointerException("The realm can not be null or empty");
		if (nonce == null || nonce.isEmpty())
			throw new NullPointerException("The nonce can not be null or empty");

		fields = new HashMap<String, String>();

		// set the initial parameters
		setRealm(realm);
		setNonce(nonce);
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
		return get(REALM);
	}

	public String getNonce()
	{
		return get(NONCE);
	}

	public String getQopOptions()
	{
		return get(QOP_OPTIONS);
	}

	public String getAlgorithm()
	{
		return get(ALGORITHM);
	}

	public String getOpaque()
	{
		return get(OPAQUE);
	}

	public String getAuthParam()
	{
		return get(AUTH_PARAM);
	}

	public void setRealm( String realm )
	{
		if (realm == null || realm.isEmpty())
			remove(REALM);
		else
			set(REALM, realm);
	}

	public void setNonce( String nonce )
	{
		if (nonce == null || nonce.isEmpty())
			remove(NONCE);
		else
			set(NONCE, nonce);
	}

	public void setQopOptions( String qop )
	{
		if (qop == null || qop.isEmpty())
			remove(QOP_OPTIONS);
		else
			set(QOP_OPTIONS, qop);
	}

	public void setAlgorithm( String algorithm )
	{
		if (algorithm == null || algorithm.isEmpty())
			remove(ALGORITHM);
		else
			set(ALGORITHM, algorithm);
	}

	public void setOpaque( String opaque )
	{
		if (opaque == null || opaque.isEmpty())
			remove(OPAQUE);
		else
			set(OPAQUE, opaque);
	}

	public void setAuthParam( String param )
	{
		if (param == null || param.isEmpty())
			remove(AUTH_PARAM);
		else
			set(AUTH_PARAM, param);
	}

	protected void set( String name, String value )
	{
		fields.put(name, value);
	}

	protected String get( String name )
	{
		return fields.get(name);
	}

	protected void remove( String name )
	{
		fields.remove(name);
	}

	protected boolean contains( String name )
	{
		return fields.containsKey(name);
	}
	
}
