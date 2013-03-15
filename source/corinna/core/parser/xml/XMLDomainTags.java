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


public enum XMLDomainTags
{

	DOMAIN("domain"),

	SERVER("server"),
	
	SERVERS("servers"),

	SERVER_NAME("name"),

	SERVER_CLASS("class"),
	
	SERVICES("services"),
	
	SERVICE("service"),
	
	SERVICE_NAME("name"),

	SERVICE_CLASS("class"),
	
	ADD_SERVICE("add-service"),
	
	CONNECTORS("connectors"),
	
	CONNECTOR("connector"),
	
	CONNECTOR_NAME("name"),

	CONNECTOR_CLASS("class"),
	
	CONNECTOR_HOST_NAME("hostname"),
	
	CONNECTOR_PORT("port"),
	
	ADD_CONNECTOR("add-connector"),
	
	INIT_PARAMETERS("init-parameters"),
	
	UNKNOW(""), 
	
	BEANS("beans"), 
	
	BEAN("bean"), 
	
	BEAN_CLASS("class"), 
	
	CONTEXTS("contexts"), 
	
	BINDLETS("bindlets"), 
	
	ADD_CONTEXT("add-context"), 
	
	BINDLET("bindlet"), 
	
	BINDLET_CLASS("class"), 
	
	CONTEXT("context"), 
	
	CONTEXT_CLASS("class"), 
	
	ADD_BINDLET("add-bindlet"), 
	
	CONTEXT_PARAMETERS("init-parameters"), 
	
	PARAMETER("parameter"), 
	
	PARAMETER_NAME("name"), 
	
	PARAMETER_VALUE("value");
	
	private String tag;
	
	private XMLDomainTags( String tag )
	{
		this.tag = tag;
	}

	public String getTagName()
	{
		return tag;
	}
	
	public static XMLDomainTags valueOfTag( String tag )
	{
		if (tag == null || tag.isEmpty()) return XMLDomainTags.UNKNOW;
		
		for ( XMLDomainTags current : values() )
		{
			String value = current.getTagName();
			if (value != null && current.getTagName().equalsIgnoreCase(tag)) return current;
		}
		return UNKNOW;
	}
	
	@Override
	public String toString()
	{
		return tag;
	}
	
}
