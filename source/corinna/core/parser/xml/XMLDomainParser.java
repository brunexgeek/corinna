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


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import corinna.bean.BeanConfig;
import corinna.bean.BeanManager;
import corinna.bean.IServiceBean;
import corinna.core.ContextConfig;
import corinna.core.Domain;
import corinna.core.IBasicConfig;
import corinna.core.IBindletRegistration;
import corinna.core.IContext;
import corinna.core.IContextConfig;
import corinna.core.IDomain;
import corinna.core.IServer;
import corinna.core.IServerConfig;
import corinna.core.IService;
import corinna.core.IServiceConfig;
import corinna.core.Server;
import corinna.core.ServerConfig;
import corinna.core.ServiceConfig;
import corinna.core.parser.IDomainParser;
import corinna.exception.ParseException;
import corinna.network.AdapterConfig;
import corinna.network.ConnectorConfig;
import corinna.network.IAdapter;
import corinna.network.IConnector;
import corinna.util.ResourceLoader;
import corinna.util.conf.ISection;
import corinna.util.conf.Section;

public class XMLDomainParser implements IDomainParser
{

	private Logger serverLog = Logger.getLogger(XMLDomainParser.class);
	
	private InputStream input;

	List<ConnectorEntry> connectorList = null;
	
	List<ServerEntry> serverList = null;
	
	Map<String, ServiceEntry> serviceList = null;
	
	Map<String, BindletEntry> bindletList = null;
	
	Map<String, ContextEntry> contextList = null;
	
	Map<String, AdapterEntry> adapterList = null;
	
	List<BeanEntry> beanList = null;
	
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
		Element element, root;

		root = document.getDocumentElement();

		// get the domain name
		if (root == null)
			throw new ParseException("The '" + XMLDomainTags.DOMAIN + "' is required");
		String domainName = root.getAttribute("name");
		if (domainName == null || domainName.isEmpty()) domainName = "default";
		
		// found the registred adapters
		element = getElementByTag(root, XMLDomainTags.ADAPTERS);
		if (element == null)
			adapterList = null;
		else
			adapterList = parseAdapters(element);
		
		// found the registred connectors
		element = getElementByTag(root, XMLDomainTags.CONNECTORS);
		if (element == null)
			throw new ParseException("The '" + XMLDomainTags.CONNECTORS + "' is required");
		connectorList = parseConnectors(element);
		
		// found the registred servers
		element = getElementByTag(root, XMLDomainTags.SERVERS);
		if (element == null)
			throw new ParseException("The '" + XMLDomainTags.SERVERS + "' is required");
		serverList = parseServers(element);

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

		// found the registred beans
		element = getElementByTag(root, XMLDomainTags.BEANS);
		if (element != null)
			beanList = parseBeans(element);
		else
			beanList = null;
		
		IDomain domain = new Domain(domainName);
		createServers(domain);
		createNetworkConnectors(domain);
		createBeans();

		return domain;
	}

	protected void createAdapter( IConnector connector, String adapterName ) throws ParseException
	{
		if (adapterList == null) return;
		try
		{
			// create the requested adapter
			AdapterEntry entry = adapterList.get(adapterName);
			if (entry == null)
				throw new ParseException("Adapter '" + adapterName + "' not found.");
			Class<?> classRef = Class.forName(entry.getClassName());
			Constructor<?> ctor = classRef.getConstructor(IAdapter.CONSTRUCTOR_ARGS);
			IAdapter adapter = (IAdapter)ctor.newInstance( entry.getConfig() );
			// insert the adapter in the connector
			connector.addAdapter(adapter);
		} catch (Exception e)
		{
			throw new ParseException("Error creating a service instance", e);
		}
	}
	
	protected void createBeans( ) throws ParseException
	{
		if (beanList == null) return;
		
		try
		{
			for (BeanEntry entry : beanList)
			{
				// create the current server
				Class<?> classRef = Class.forName(entry.getClassName());
				Constructor<?> ctor = classRef.getConstructor(IServiceBean.CONSTRUCTOR_ARGS);
				IServiceBean bean = (IServiceBean)ctor.newInstance( entry.getConfig() );
				// insert the server in domain
				BeanManager.getInstance().addBean(bean);
			}
		} catch (Exception e)
		{
			throw new ParseException("Error creating a bean instance", e);
		}
	}
	
	protected void createNetworkConnectors( IDomain domain ) throws ParseException
	{
		try
		{
			for (ConnectorEntry entry : connectorList)
			{
				// create the current server
				Class<?> classRef = Class.forName(entry.getClassName());
				Constructor<?> ctor = classRef.getConstructor(IConnector.CONSTRUCTOR_ARGS);
				IConnector connector = (IConnector)ctor.newInstance( entry.getConfig() );
				// create the associated adapters
				for (String adapterName : entry.getAdapters() )
					createAdapter(connector, adapterName);
				// insert the server in domain
				domain.addConnector(connector);
			}
		} catch (Exception e)
		{
			throw new ParseException("Error creating a network connector instance", e);
		}
	}
	
	protected void createServers( IDomain domain ) throws ParseException
	{
		try
		{
			for (ServerEntry entry : serverList)
			{
				// create the current server
				Class<?> classRef = Class.forName(entry.getClassName());
				Constructor<?> ctor = classRef.getConstructor(IServer.CONSTRUCTOR_ARGS);
				IServer server = (IServer)ctor.newInstance( entry.getConfig() );
				// insert the server in domain
				domain.addServer(server);
				// create the associated services
				for (String serviceName : entry.getServices() )
					createService(server, serviceName);
			}
		} catch (Exception e)
		{
			throw new ParseException("Error creating a server instance", e);
		}
	}
	
	protected void createService( IServer server, String serviceName ) throws ParseException
	{
		try
		{
			// create the requested service
			ServiceEntry entry = serviceList.remove(serviceName);
			if (entry == null)
				throw new ParseException("Service '" + serviceName + "' not found.");
			Class<?> classRef = Class.forName(entry.getClassName());
			Constructor<?> ctor = classRef.getConstructor(IService.CONSTRUCTOR_ARGS);
			IService service = (IService)ctor.newInstance( entry.getConfig(), server );
			// insert the service in server
			server.addService(service);
			// create the associated contexts
			for (String contextName : entry.getContexts() )
				createContext(service, contextName);
		} catch (Exception e)
		{
			throw new ParseException("Error creating a service instance", e);
		}
	}
	
	protected void createContext( IService service, String contextName ) throws ParseException
	{
		try
		{
			// create the requested context
			ContextEntry entry = contextList.remove(contextName);
			if (entry == null)
				throw new ParseException("Context '" + contextName + "' not found.");
			Class<?> classRef = Class.forName(entry.getClassName());
			Constructor<?> ctor = classRef.getConstructor(IContext.CONSTRUCTOR_ARGS);
			IContext<?,?> context = (IContext<?,?>)ctor.newInstance( entry.getConfig(), service );
			// insert the context in service
			service.addContext(context);
			// create the associated contexts
			for (String bindletName : entry.getBindlets() )
				createBindlet(context, bindletName);
		} catch (Exception e)
		{
			throw new ParseException("Error creating a context instance", e);
		}
	}
	
	protected void createBindlet( IContext<?,?> context, String bindletName ) throws ParseException
	{
		try
		{
			// register the requested bindlet
			BindletEntry entry = bindletList.remove(bindletName);
			if (entry == null)
				throw new ParseException("Bindlet '" + bindletName + "' not found.");
			IBindletRegistration bindlet = context.addBindlet(bindletName, entry.getClassName());
			// insert the bindlet registration parameters
			for (String param : entry.getConfig().getKeys() )
				bindlet.setBindletParameter(param, entry.getConfig().getValue(param, ""));
		} catch (Exception e)
		{
			throw new ParseException("Error creating a bindlet registration", e);
		}
	}
	
	private List<BeanEntry> parseBeans( Element parent ) throws ParseException
	{
		if (!isTag(parent, XMLDomainTags.BEANS)) throw new ParseException("Invalid tag name");

		List<BeanEntry> output = new LinkedList<BeanEntry>();
		NodeList list = getElementsByTag(parent, XMLDomainTags.BEAN);

		for (int i = 0; i < list.getLength(); ++i)
		{
			Element element = (Element) list.item(i);

			// get the bean name
			String beanName = getElementAttribute(element, "name");
			// get the bean class name
			String beanClassName = getTagContent(element, XMLDomainTags.BEAN_CLASS);
			// create the configuration object and get the bean parameters
			BeanConfig config = new BeanConfig(beanName);
			parseParameters(element, XMLDomainTags.INIT_PARAMETERS, config);

			try
			{
				BeanEntry entry = new BeanEntry(beanClassName, config);
				// add the current connector in output list
				output.add(entry);
			} catch (Exception e)
			{
				throw new ParseException("Error creating server instance for '"
					+ beanClassName + "'", e);
			}
		}
		return output;
	}
	
	private Map<String, AdapterEntry> parseAdapters( Element parent ) throws ParseException
	{
		if (!isTag(parent, XMLDomainTags.ADAPTERS)) throw new ParseException("Invalid tag name");

		Map<String, AdapterEntry> output = new HashMap<String, AdapterEntry>();
		NodeList list = getElementsByTag(parent, XMLDomainTags.ADAPTER);

		for (int i = 0; i < list.getLength(); ++i)
		{
			Element element = (Element) list.item(i);

			// get the connector name
			String adapterName = getElementAttribute(element, "name");
			// get the connector class name
			String adapterClassName = getTagContent(element, XMLDomainTags.ADAPTER_CLASS);
			// create the configuration object and get the connector parameters
			AdapterConfig config = new AdapterConfig(adapterName);
			parseParameters(element, XMLDomainTags.INIT_PARAMETERS, config);

			try
			{
				AdapterEntry entry = new AdapterEntry(adapterClassName, config);
				// add the current connector in output list
				output.put(adapterName, entry);
			} catch (Exception e)
			{
				throw new ParseException("Error creating adapter instance for '"
					+ adapterClassName + "'", e);
			}
		}
		return output;
	}
	
	private List<ConnectorEntry> parseConnectors( Element parent ) throws ParseException
	{
		if (!isTag(parent, XMLDomainTags.CONNECTORS)) throw new ParseException("Invalid tag name");

		List<ConnectorEntry> output = new LinkedList<ConnectorEntry>();
		NodeList list = getElementsByTag(parent, XMLDomainTags.CONNECTOR);

		for (int i = 0; i < list.getLength(); ++i)
		{
			Element element = (Element) list.item(i);
			Element temp;

			// get the connector name
			String connectorName = getElementAttribute(element, "name");
			// get the connector class name
			String connectorClassName = getTagContent(element, XMLDomainTags.CONNECTOR_CLASS);
			// get the connector address
			String connectorHostName = getTagContent(element, XMLDomainTags.CONNECTOR_HOST_NAME);
			String connectorPort = getTagContent(element, XMLDomainTags.CONNECTOR_PORT);
			int port = stringToInt(connectorPort, 10, -1);
			// create the configuration object and get the connector parameters
			ConnectorConfig config = new ConnectorConfig(connectorName, connectorHostName, port);
			parseParameters(element, XMLDomainTags.INIT_PARAMETERS, config);

			try
			{
				ConnectorEntry entry = new ConnectorEntry(connectorClassName, config);
				
				// found the associated contexts
				temp = getElementByTag(element, XMLDomainTags.ADAPTERS);
				if (temp != null)
				{
					NodeList binds = getElementsByTag(temp, XMLDomainTags.ADD_ADAPTER);
					// for each associated context...
					for (int j = 0; j < binds.getLength(); ++j)
					{
						Element current = (Element) binds.item(j);
						String name = current.getTextContent();
						if (name == null || name.isEmpty()) continue;
						entry.addAdapter(name);
					}
				}
				
				// add the current connector in output list
				output.add(entry);
			} catch (Exception e)
			{
				throw new ParseException("Error creating server instance for '"
					+ connectorClassName + "'", e);
			}
		}
		return output;
	}

	private List<ServerEntry> parseServers( Element parent ) throws ParseException
	{
		if (!isTag(parent, XMLDomainTags.SERVERS)) throw new ParseException("Invalid tag name");

		List<ServerEntry> output = new LinkedList<ServerEntry>();
		NodeList list = getElementsByTag(parent, XMLDomainTags.SERVER);

		for (int i = 0; i < list.getLength(); ++i)
		{
			Element element = (Element) list.item(i);
			Element temp;

			// get the server name
			String serverName = getElementAttribute(element, "name");
			// get the server class name
			String serverClassName = getTagContent(element, XMLDomainTags.SERVER_CLASS, 
				Server.class.getName());
			// create the configuration object and get the server parameters
			IServerConfig config = new ServerConfig(serverName);
			parseParameters(element, XMLDomainTags.INIT_PARAMETERS, config);

			try
			{
				ServerEntry entry = new ServerEntry(serverClassName, config);

				// found the associated contexts
				temp = getElementByTag(element, XMLDomainTags.SERVICES);
				if (temp == null)
					throw new ParseException("The '" + XMLDomainTags.SERVICES
						+ "' tag is required in the service");
				NodeList binds = getElementsByTag(temp, XMLDomainTags.ADD_SERVICE);
				// for each associated context...
				for (int j = 0; j < binds.getLength(); ++j)
				{
					Element current = (Element) binds.item(j);
					String name = current.getTextContent();
					if (name == null || name.isEmpty()) continue;
					entry.addService(name);
				}

				// add the current service in output list
				output.add(entry);
			} catch (Exception e)
			{
				throw new ParseException("Error creating server instance for '" + serverClassName
					+ "'", e);
			}
		}
		return output;
	}

	private String getTagContent( Element parent, XMLDomainTags tag, String defaultValue )
	{
		Element temp = getElementByTag(parent, tag);
		if (temp == null)
			return defaultValue;
		else
			return temp.getTextContent();
	}
	
	private String getTagContent( Element parent, XMLDomainTags tag ) 
		throws ParseException
	{
		String value = getTagContent(parent, tag, null);
		if (value == null)
			throw new ParseException("The '" + tag + "' tag is required");
		return value;
	}
	
	private String getElementAttribute( Element element, String attName ) throws ParseException
	{
		String value = element.getAttribute(attName);
		if (value == null || value.isEmpty()) 
			throw new ParseException("The '" + attName + "' attribute is required");
		return value;
	}
	
	private void parseParameters( Element parent, XMLDomainTags tag, ISection config ) throws ParseException
	{
		Element temp = getElementByTag(parent, tag);
		if (temp == null) return;
		parseParameters(temp, config);
	}
	
	private void parseParameters( Element parent, XMLDomainTags tag, IBasicConfig config ) throws ParseException
	{
		Element temp = getElementByTag(parent, tag);
		if (temp == null) return;
		parseParameters(temp, config.getSection());
	}
	
	private Map<String, ServiceEntry> parseServices( Element parent ) throws ParseException
	{
		if (!isTag(parent, XMLDomainTags.SERVICES)) throw new ParseException("Invalid tag name");

		Map<String, ServiceEntry> output = new HashMap<String, ServiceEntry>();
		NodeList list = getElementsByTag(parent, XMLDomainTags.SERVICE);

		for (int i = 0; i < list.getLength(); ++i)
		{
			Element element = (Element) list.item(i);
			Element temp;

			// get the service name
			String serviceName = getElementAttribute(element, "name");
			// get the service class name
			String serviceClassName = getTagContent(element, XMLDomainTags.SERVICE_CLASS);
			// create the configuration object and get the service parameters
			IServiceConfig config = new ServiceConfig(serviceName);
			parseParameters(element, XMLDomainTags.INIT_PARAMETERS, config);
			
			try
			{
				ServiceEntry entry = new ServiceEntry(serviceClassName, config);

				// found the associated contexts
				temp = getElementByTag(element, XMLDomainTags.CONTEXTS);
				if (temp == null)
					throw new ParseException("The '" + XMLDomainTags.CONTEXTS
						+ "' tag is required in the service");
				NodeList binds = getElementsByTag(temp, XMLDomainTags.ADD_CONTEXT);
				// for each associated context...
				for (int j = 0; j < binds.getLength(); ++j)
				{
					Element current = (Element) binds.item(j);
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

	private Map<String, BindletEntry> parseBindlets( Element parent ) throws ParseException
	{
		if (!isTag(parent, XMLDomainTags.BINDLETS)) throw new ParseException("Invalid tag name");

		Map<String, BindletEntry> output = new HashMap<String, BindletEntry>();
		NodeList list = getElementsByTag(parent, XMLDomainTags.BINDLET);

		for (int i = 0; i < list.getLength(); ++i)
		{
			Element element = (Element) list.item(i);

			// get the bindlet name
			String bindletName = getElementAttribute(element, "name");
			// get the bindlet class name
			String bindletClassName = getTagContent(element, XMLDomainTags.BINDLET_CLASS);
			// create the registred init parameters for this bindlet
			ISection config = new Section("Parameters");
			parseParameters(element, XMLDomainTags.INIT_PARAMETERS, config);

			try
			{
				// create and fill a new bindlet entry
				BindletEntry entry = new BindletEntry(bindletName, bindletClassName, config);
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
	private Map<String, ContextEntry> parseContexts( Element parent ) throws ParseException
	{
		if (!isTag(parent, XMLDomainTags.CONTEXTS)) throw new ParseException("Invalid tag name");

		Map<String, ContextEntry> output = new HashMap<String, ContextEntry>();
		NodeList list = getElementsByTag(parent, XMLDomainTags.CONTEXT);

		// for each bindlet context...
		for (int i = 0; i < list.getLength(); ++i)
		{
			Element element = (Element) list.item(i);
			Element temp;

			// get the bindlet context name
			String contextName = getElementAttribute(element, "name");
			// get the bindlet context class name
			String contextClassName = getTagContent(element, XMLDomainTags.CONTEXT_CLASS);
			// create the configuration object and get the service parameters
			IContextConfig config = new ContextConfig(contextName);
			parseParameters(element, XMLDomainTags.INIT_PARAMETERS, config);

			try
			{
				ContextEntry entry = new ContextEntry(contextClassName, config);

				// found the associated contexts
				temp = getElementByTag(element, XMLDomainTags.BINDLETS);
				if (temp == null)
					throw new ParseException("The '" + XMLDomainTags.CONTEXT_CLASS
						+ "' tag is required in the context");
				NodeList binds = getElementsByTag(temp, XMLDomainTags.ADD_BINDLET);
				// for each associated bindlet...
				for (int j = 0; j < binds.getLength(); ++j)
				{
					Element current = (Element) binds.item(j);
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

	private void parseParameters( Element parent, ISection config ) throws ParseException
	{
		if (parent == null || config == null) return;

		if (!isTag(parent, XMLDomainTags.INIT_PARAMETERS)
			&& !isTag(parent, XMLDomainTags.CONTEXT_PARAMETERS))
			throw new ParseException("Invalid tag name");

		NodeList list = getElementsByTag(parent, XMLDomainTags.PARAMETER);

		for (int i = 0; i < list.getLength(); ++i)
		{
			Element element = (Element) list.item(i);

			// get the parameter name
			String paramName = getTagContent(element, XMLDomainTags.PARAMETER_NAME);
			// get the parameter value
			String paramValue = getTagContent(element, XMLDomainTags.PARAMETER_VALUE);
			// create the parameter
			config.setValue(paramName, paramValue);
		}
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

	private int stringToInt( String value, int radix, int defaultValue )
	{
		try
		{
			return Integer.parseInt(value, radix);
		} catch (Exception e)
		{
			return defaultValue;
		}
	}
	
}
