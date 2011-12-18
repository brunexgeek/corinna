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

package corinna.core;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import corinna.core.parser.xml.BindletEntry;
import corinna.core.parser.xml.ContextEntry;
import corinna.core.parser.xml.ServerEntry;
import corinna.core.parser.xml.ServiceEntry;
import corinna.exception.ParseException;
import corinna.network.INetworkConnector;
import corinna.util.ResourceLoader;

// TODO: move to 'corinna.core.parser.xml'
public class XMLDomainParser implements IDomainParser
{

	private InputStream input;

	public XMLDomainParser( String fileName ) throws IOException
	{
		if (fileName == null)
			throw new IllegalArgumentException("The filename can not be null or empty");

		input = ResourceLoader.getResourceAsStream(fileName);
	}

	@Override
	public IDomain parse() throws ParseException
	{
		Document dom = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try
		{
			// using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// parse using builder to get DOM representation of the XML file
			dom = db.parse(input);
		} catch (Exception e)
		{
			throw new ParseException("Error parsing XML domain description file", e);
		}
		return parseDocument(dom);
	}
	
	private IDomain parseDocument( Document document ) throws ParseException
	{
		Map<String, ServerEntry> serverList;
		Map<String, ServiceEntry> serviceList;
		Map<String, BindletEntry> bindletList = null;
		Map<String, ContextEntry> contextList;
		List<INetworkConnector<?, ?>> connectorList = null;
		Element element, root;

		root = document.getDocumentElement();

		// get the domain name
		element = getElementByTag(root, XMLDomainTags.DOMAIN);
		if (element == null)
			throw new ParseException("The '" + XMLDomainTags.DOMAIN + "' is required");
		String domainName = element.getAttribute("name");
		if (domainName == null || domainName.isEmpty()) domainName = "corinna";
		
		// found the registred services
		element = getElementByTag(root, XMLDomainTags.SERVICES);
		if (element == null)
			throw new ParseException("The '" + XMLDomainTags.SERVICES + "' is required");
		serviceList = parseServices(element);
		
		// found the registred contexts
		element = getElementByTag(root, XMLDomainTags.CONTEXTS);
		if (element == null)
			throw new ParseException("The '" + XMLDomainTags.CONTEXTS + "' is required");
		contextList = parseContexts(element);
		
		// found the registred bindlets
		element = getElementByTag(root, XMLDomainTags.BINDLETS);
		if (element == null)
			throw new ParseException("The '" + XMLDomainTags.BINDLETS + "' is required");
		bindletList = parseBindlets(element);

		IDomain domain = new Domain(domainName);
		
		return domain;
	}


	private Map<String,ServiceEntry> parseServices( Element parent ) throws ParseException
	{
		if (!isTag(parent, XMLDomainTags.SERVICES)) throw new ParseException("Invalid tag name");

		Map<String,ServiceEntry> output = new HashMap<String,ServiceEntry>();
		NodeList list = getElementsByTag(parent, XMLDomainTags.SERVICE);

		for (int i = 0; i < list.getLength(); ++i)
		{
			Element element = (Element) list.item(i);
			Element temp;

			// get the service name
			String serviceName = element.getAttribute("name");
			if (serviceName == null) throw new ParseException("The 'name' attribute is required");

			// get the service class name
			temp = getElementByTag(element, XMLDomainTags.SERVICE_CLASS);
			if (temp == null)
				throw new ParseException("The '" + XMLDomainTags.SERVICE_CLASS
					+ "' tag is required");
			String serviceClassName = temp.getTextContent();

			try
			{
				ServiceEntry entry = new ServiceEntry(serviceName, serviceClassName);
				
				// found the associated contexts
				temp = getElementByTag(element, XMLDomainTags.CONTEXTS);
				if (temp == null)
					throw new ParseException("The '" + XMLDomainTags.CONTEXTS
						+ "' tag is required in the service");
				NodeList binds = getElementsByTag(temp, XMLDomainTags.ADD_CONTEXT);
				// for each associated context...
				for (int j = 0; j < list.getLength(); ++j)
				{
					Element current = (Element) binds.item(i);
					String name = current.getTextContent();
					if (name == null || name.isEmpty()) continue;
					entry.addContext(name);
				}
				
				// add the current service in output list
				output.put(serviceName, entry);
			} catch (Exception e)
			{
				throw new ParseException("Error creating server instance for '" + serviceClassName
					+ "'", e);
			}
		}
		return output;
	}

	/**
	 * Parse bindlets.
	 * 
	 * @param parent
	 * @return
	 * @throws ParseException
	 */
	private Map<String, BindletEntry> parseBindlets( Element parent ) throws ParseException
	{
		if (!isTag(parent, XMLDomainTags.BINDLETS)) throw new ParseException("Invalid tag name");

		Map<String, BindletEntry> output = new HashMap<String, BindletEntry>();
		NodeList list = getElementsByTag(parent, XMLDomainTags.BINDLET);

		for (int i = 0; i < list.getLength(); ++i)
		{
			Element element = (Element) list.item(i);

			// get the bindlet name
			String bindletName = element.getAttribute("name");
			if (bindletName == null) throw new ParseException("The 'name' attribute is required");

			// get the bindlet class name
			Element temp = getElementByTag(element, XMLDomainTags.BINDLET_CLASS);
			if (temp == null)
				throw new ParseException("The '" + XMLDomainTags.BINDLET_CLASS
					+ "' tag is required");
			String bindletClassName = temp.getTextContent();

			// create the registred init parameters for this bindlet
			temp = getElementByTag(element, XMLDomainTags.INIT_PARAMETERS);
			List<IParameter> params = parseParameters(temp);

			try
			{
				// create and fill a new bindlet entry
				BindletEntry entry = new BindletEntry(bindletName, bindletClassName);
				for (IParameter current : params)
					entry.setParameter(current.getName(), current.getValue());
				
				output.put(bindletName, entry);
			} catch (Exception e)
			{
				throw new ParseException("Error registring the bindlet for '" + bindletClassName
					+ "'", e);
			}
		}
		return output;
	}

	/**
	 * Parse bindlet contexts.
	 * 
	 * @param parent
	 * @return
	 * @throws ParseException
	 */
	private Map<String,ContextEntry> parseContexts( Element parent ) throws ParseException
	{
		if (!isTag(parent, XMLDomainTags.CONTEXTS)) throw new ParseException("Invalid tag name");

		Map<String,ContextEntry> output = new HashMap<String,ContextEntry>();
		NodeList list = getElementsByTag(parent, XMLDomainTags.CONTEXT);

		// for each bindlet context...
		for (int i = 0; i < list.getLength(); ++i)
		{
			Element element = (Element) list.item(i);

			// get the bindlet context name
			String contextName = element.getAttribute("name");
			if (contextName == null) throw new ParseException("The 'name' attribute is required");

			// get the bindlet context class name
			Element temp = getElementByTag(element, XMLDomainTags.CONTEXT_CLASS);
			if (temp == null)
				throw new ParseException("The '" + XMLDomainTags.CONTEXT_CLASS
					+ "' tag is required");
			String contextClassName = temp.getTextContent();

			// create the registred context parameters for this bindlet context
			temp = getElementByTag(element, XMLDomainTags.CONTEXT_PARAMETERS);
			List<IParameter> params = parseParameters(temp);

			try
			{
				ContextEntry entry = new ContextEntry(contextName, contextClassName);
				// set the context parameters
				for (IParameter current : params)
					entry.setParameter(current.getName(), current.getValue());
											
				// found the associated contexts
				temp = getElementByTag(element, XMLDomainTags.BINDLETS);
				if (temp == null)
					throw new ParseException("The '" + XMLDomainTags.CONTEXT_CLASS
						+ "' tag is required in the context");
				NodeList binds = getElementsByTag(temp, XMLDomainTags.ADD_BINDLET);
				// for each associated context...
				for (int j = 0; j < list.getLength(); ++j)
				{
					Element current = (Element) binds.item(i);
					String name = current.getTextContent();
					if (name == null || name.isEmpty()) continue;
					entry.addBindlet(name);
				}
				
				output.put(contextName, entry);
			} catch (Exception e)
			{
				throw new ParseException("Error creating bindlet instance for '" + contextClassName
					+ "'", e);
			}
		}
		return output;
	}

	/*@SuppressWarnings("unchecked")
	protected void registerBindlets( List<String> bindletNames,
		Map<String, BindletEntry> bindletInfos, IContext<?, ?> context )
		throws ParseException
	{
		for (String currentName : bindletNames)
		{
			BindletEntry entry = bindletInfos.get(currentName);
			if (entry == null) throw new ParseException("The referenced bindlet does not exist: " + currentName);
			
			try
			{
				context.addBindlet(entry.getBindletName(), entry.getBindletClassName());
			} catch (Exception e)
			{
				throw new ParseException("Error creating bindlet instance for '" + entry.getBindletClassName()
					+ "'", e);
			}
		}
	}*/

	private List<IParameter> parseParameters( Element parent ) throws ParseException
	{
		List<IParameter> output = new LinkedList<IParameter>();
		
		if (parent == null) return output;

		if (!isTag(parent, XMLDomainTags.INIT_PARAMETERS)
			&& !isTag(parent, XMLDomainTags.CONTEXT_PARAMETERS))
			throw new ParseException("Invalid tag name");
		
		NodeList list = getElementsByTag(parent, XMLDomainTags.PARAMETER);

		for (int i = 0; i < list.getLength(); ++i)
		{
			Element element = (Element) list.item(i);

			// get the parameter name
			String paramName = element.getAttribute("name");
			if (paramName == null)
				throw new ParseException("The 'name' attribute of tag " + XMLDomainTags.PARAMETER
					+ "' is required");
			// get the parameter value
			String paramValue = element.getAttribute("value");
			if (paramValue == null)
				throw new ParseException("The 'value' attribute of tag " + XMLDomainTags.PARAMETER
					+ "' is required");
			// create the parameter
			IParameter param = new Parameter(paramName, paramValue);
			output.add(param);
		}
		return output;
	}

	protected Element getElementByTag( Element parent, XMLDomainTags tag )
	{
		NodeList list = parent.getElementsByTagName(tag.getTagName());
		if (list == null) return null;
		for (int i = 0; i < list.getLength(); ++i)
		{
			Element current = (Element) list.item(i);
			if (current.getParentNode() == parent) return current;
		}
		return null;
	}

	protected NodeList getElementsByTag( Element parent, XMLDomainTags tag )
	{
		NodeList list = parent.getElementsByTagName(tag.getTagName());
		return list;
	}

	protected boolean isTag( Element element, XMLDomainTags tag )
	{
		if (element == null || tag == null) return false;
		return element.getTagName().equalsIgnoreCase(tag.getTagName());
	}
	
}
