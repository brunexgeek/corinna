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

import java.util.Map;


public class KeyValueParser
{
	
	private KeyValueParser()
	{
	}
	
	public static void parse( String data, Map<String,String> output, KeyValueParserConfig config )
	{
		if (data == null) return;

		int start = 0;
		int end = data.indexOf(config.getPairSeparator(), start);
		if (end < 0) end = data.length();

		while (start >= 0)
		{
			// don't process empty strings
			if (start != end)
			{
				// find the position of key-value separator
				int pos = data.indexOf(config.getKeyValueSeparator(), start);
				if (pos == -1) pos = end;
				// extract the key and value
				String key = data.substring(start, pos).trim();
				String value = "";
				if (pos+1 < end)
				{
					value = data.substring(pos + 1, end).trim();
					if (config.isQuotedValues())
					{
						int s = 0, l = value.length();
						if (value.startsWith("\"")) s = 1;
						if (value.endsWith("\"")) l--;
						if (l - 1 > s) value = value.substring(s, l);
					}
				}
				if (value.isEmpty() && !config.isAllowEmptyValues()) continue;
				
				output.put(key, value);
			}
			// find the next pair
			start = data.indexOf(config.getPairSeparator(), start);
			if (start < 0) break;
			start++;
			end = data.indexOf(config.getPairSeparator(), start);
			if (end < 0) end = data.length();
		}
	}
	

	
}
