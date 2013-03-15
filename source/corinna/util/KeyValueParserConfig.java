/*
 * Copyright 2011-2013 Bruno Ribeiro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
