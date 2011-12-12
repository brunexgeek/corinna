/*
 * Copyright 2011 Bruno Ribeiro <brunei@users.sourceforge.net>
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

package corinna.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Armazena o conjunto de capacidades que um protocolo possui.
 * 
 * @author Bruno Ribeiro &lt;brunoc@cpqd.com.br&gt;
 * @since 1.0
 */
public abstract class Capabilities 
{

	ConcurrentMap<String,Boolean> capabilities = null;

	
	public Capabilities( Map<String,Boolean> map )
	{
		capabilities = new ConcurrentHashMap<String,Boolean>(map);
	}
	
	
	public boolean haveCapability( String capability ) 
	{
		Boolean value = capabilities.get(capability);
		
		if (value == null) value = false;
		return value;
	}
	
	
}
