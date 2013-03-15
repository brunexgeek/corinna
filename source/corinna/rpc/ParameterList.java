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
package corinna.rpc;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;



/**
 * Armazena os parâmetros de uma chamada de procedimento remoto.
 * 
 * @author bruno
 * 
 */
public class ParameterList extends AbstractParameterList implements ISerializable<String>
{

	private static final long serialVersionUID = -2960797135155777975L;

	public static final String PAIR_SEPARATOR = "&";

	public static final String KEYVALUE_SEPARATOR = "=";
	
	public ParameterList()
	{
	}
	
	public ParameterList( String data )
	{
		deserialize(data);
	}
	
	public ParameterList( Charset charset, String data )
	{
		super(charset);
		deserialize(data);	
	}
	
	public ParameterList( Charset charset )
	{
		super(charset);
	}

	public void removeValue( String key )
	{
		parameters.remove(key);
	}

	public void setCharset( Charset charset )
	{
		this.charset = charset;
	}

	@Override
	public String toString()
	{
		return serialize();
	}
		
	protected static String encode( Object text, Charset encoding )
	{
		try
		{
			return URLEncoder.encode(text.toString(), encoding.displayName());
		} catch (Exception e)
		{
			return null;
		}
	}
	
	protected static String decode( Object text, Charset encoding )
	{
		try
		{
			return URLDecoder.decode(text.toString(), encoding.displayName());
		} catch (Exception e)
		{
			return null;
		}
	}

	@Override
	public String serialize()
	{
		StringBuffer sb = new StringBuffer();
		int count = parameters.size();
		
		for ( Map.Entry<String, Object> entry : parameters.entrySet())
		{
			sb.append(entry.getKey());
			sb.append(KEYVALUE_SEPARATOR);
			sb.append( encode( entry.getValue(), charset ) );
			if (--count > 0) sb.append(PAIR_SEPARATOR);
		}
		return sb.toString();
	}

	@Override
	public void deserialize( String data )
	{
		parameters.clear();
		parseString(this, data, PAIR_SEPARATOR, KEYVALUE_SEPARATOR);
	}
	
	public static void parseString( AbstractParameterList list, String data, String pairSeparator, 
		String keyValueSeparator )
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
				String value = decode( data.substring(pos + 1, end), list.getCharset() );
				list.setValue(key, value);
			}
			// find the next pair
			start = data.indexOf(pairSeparator, start);
			if (start < 0) break;
			start++;
			end = data.indexOf(pairSeparator, start);
			if (end < 0) end = data.length();
		}
	}
	
	public static AbstractParameterList parseString( String data, String pairSeparator, 
		String keyValueSeparator )
	{
		AbstractParameterList list = new ParameterList();
		parseString(list, data, pairSeparator, keyValueSeparator);
		return list;
	}
}
