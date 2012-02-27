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

package corinna.core.parser.xml;

//TODO: move to 'corinna.core.parser.xml'
public enum XMLDomainTags
{

	DOMAIN("domain"),

	SERVER("server"),
	
	SERVERS("servers"),

	SERVER_NAME("server-name"),

	SERVER_CLASS("server-class"),
	
	SERVICES("services"),
	
	SERVICE("service"),
	
	SERVICE_NAME("service-name"),

	SERVICE_CLASS("service-class"),
	
	CONNECTORS("connectors"),
	
	CONNECTOR("connector"),
	
	CONNECTOR_NAME("connector-name"),

	CONNECTOR_CLASS("connector-class"),
	
	CONNECTOR_HOST_NAME("connector-hostname"),
	
	CONNECTOR_PORT("connector-port"),
	
	INIT_PARAMETERS("init-parameters"),
	
	CONTEXT_PARAMETERS("init-parameters"),
	
	ADD_BINDLET("add-bindlet"),
	
	PARAMETER("parameter"),
	
	PARAMETER_NAME("parameter-name"),
	
	PARAMETER_VALUE("parameter-value"),
	
	CONTEXTS("contexts"),
	
	CONTEXT_NAME("context-name"),
	
	CONTEXT_CLASS("context-class"),
	
	CONTEXT("context"),
	
	BINDLETS("bindlets"),
	
	BINDLET("bindlet"),
	
	BINDLET_NAME("bindlet-name"),
	
	BINDLET_CLASS("bindlet-class"),
	
	ADD_CONTEXT("add-context"),
	
	ADD_SERVICE("add-service"),
	
	BEANS("beans"),
	
	BEAN("bean"),
		
	BEAN_NAME("bean-name"),
	
	BEAN_CLASS("bean-class"),
	
	ADAPTERS("adapters"),
	
	ADAPTER("adapter"),
	
	ADAPTER_CLASS("adapter-class"),
	
	ADD_ADAPTER("add-adapter"),
	
	UNKNOW(null);
	
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
	
}
