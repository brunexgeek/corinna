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
package corinna.core.parser.xml;


import java.util.LinkedList;
import java.util.List;

import corinna.core.IBasicConfig;


public class ServerEntry extends BasicEntry
{

	private List<String> services;
	
	private List<String> connectors;

	public ServerEntry( String className, IBasicConfig config )
	{
		super(className, config);
		services = new LinkedList<String>();
		connectors = new LinkedList<String>();
	}

	public void addService( String name )
	{
		services.add(name);
	}

	public void addConnector( String name )
	{
		connectors.add(name);
	}
	
	public List<String> getServices()
	{
		return services;
	}

	public List<String> getConnectors()
	{
		return connectors;
	}
	
}
