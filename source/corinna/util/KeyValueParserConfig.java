package corinna.util;


public class KeyValueParserConfig
{
	
	private static final boolean DEFAULT_QUOTEDVALUES = false;
	
	private static final String DEFAULT_PAIRSEPARATOR = "&";
	
	private static final String DEFAULT_KEYVALUESEPARATOR = "=";

	public static final boolean DEFAULT_ALLOWEMPTY = false;
	
	private boolean quotedValues = DEFAULT_QUOTEDVALUES;
	
	private boolean allowEmpty = DEFAULT_ALLOWEMPTY;
	
	private String pairSeparator = DEFAULT_PAIRSEPARATOR;
	
	private String keyValueSeparator = DEFAULT_KEYVALUESEPARATOR;

	public void setQuotedValues( boolean quotedValues )
	{
		this.quotedValues = quotedValues;
	}

	public boolean isQuotedValues()
	{
		return quotedValues;
	}

	public void setPairSeparator( String pairSeparator )
	{
		this.pairSeparator = pairSeparator;
	}

	public String getPairSeparator()
	{
		return pairSeparator;
	}

	public void setKeyValueSeparator( String keyValueSeparator )
	{
		this.keyValueSeparator = keyValueSeparator;
	}

	public String getKeyValueSeparator()
	{
		return keyValueSeparator;
	}

	public void setAllowEmptyValues( boolean allowEmpty )
	{
		this.allowEmpty = allowEmpty;
	}

	public boolean isAllowEmptyValues()
	{
		return allowEmpty;
	}
	
}
