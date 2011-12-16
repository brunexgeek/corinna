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
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.bindlet.BindletConfig;
import javax.bindlet.IBindlet;
import javax.bindlet.IBindletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import corinna.exception.InvalidClassException;
import corinna.exception.ParseException;
import corinna.network.INetworkConnector;
import corinna.network.rest.RestProtocol;
import corinna.util.ResourceLoader;


public class XMLDomainParser extends DefaultHandler implements IDomainParser
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

		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try
		{

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			dom = db.parse(input);

		} catch (ParserConfigurationException pce)
		{
			pce.printStackTrace();
		} catch (SAXException se)
		{
			se.printStackTrace();
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		return parseDocument(dom);
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

	private IDomain parseDocument( Document document ) throws ParseException
	{
		List<IServer> serverList;
		Map<String, IService> serviceList;
		Map<String, BindletEntry> bindletList = null;
		Map<String, IContext<?, ?>> contextList;
		List<INetworkConnector<?, ?>> connectorList = null;
		Element element, root;

		root = document.getDocumentElement();

		// create the registred bindlets
		element = getElementByTag(root, XMLDomainTags.BINDLETS);
		if (element == null)
			throw new ParseException("The '" + XMLDomainTags.BINDLETS + "' is required");
		bindletList = parseBindlets(element);
		
		// create the registred contexts
		element = getElementByTag(root, XMLDomainTags.CONTEXTS);
		if (element == null)
			throw new ParseException("The '" + XMLDomainTags.CONTEXTS + "' is required");
		contextList = parseContexts(element, bindletList);
		
		// create the registred services
		element = getElementByTag(root, XMLDomainTags.SERVICES);
		if (element == null)
			throw new ParseException("The '" + XMLDomainTags.SERVICES + "' is required");
		serviceList = parseServices(element, contextList);

		return null;
	}


	private Map<String,IService> parseServices( Element parent, Map<String,IBindletContext<?,?>> contexts ) throws ParseException
	{
		if (!isTag(parent, XMLDomainTags.SERVICES)) throw new ParseException("Invalid tag name");

		Map<String,IService> output = new HashMap<String,IService>();
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
				// create a new server instance
				Class<?> serviceClass = Class.forName(serviceClassName);
				Constructor<?> ctor = serviceClass.getConstructor(IServer.CONSTRUCTOR_ARGS);
				IService service = (IService) ctor.newInstance(serviceName);
				
				// found the associated contexts
				NodeList binds = getElementsByTag(parent, XMLDomainTags.ADD_CONTEXT);
				// for each associated context...
				for (int j = 0; j < list.getLength(); ++j)
				{
					Element current = (Element) binds.item(i);
					String name = current.getTextContent();
					if (name == null || name.isEmpty()) continue;
					IBindletContext<?,?> context = contexts.get(name);
					if (context != null) service.addContext(context);
				}
				
				// add the current service in output list
				output.put(serviceName, service);
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
			Class<?> bindletClass = null;

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
				// get the bindlet class
				bindletClass = Class.forName(bindletClassName);
				// get the bindlet context class
				BindletContextInfo info = bindletClass.getAnnotation(BindletContextInfo.class);
				if (info == null)
					throw new InvalidClassException(
						"The bindlet class must be have the 'BindletContextInfo' annotation");
				// create and fill a new bindlet configuration
				BindletConfig config = new BindletConfig(bindletName);
				for (IParameter current : params)
					config.setBindletParameter(current.getName(), current.getValue());

				// add new bindlet and configuration in output list
				BindletEntry entry = new BindletEntry(bindletName, bindletClass, config);
				output.put(bindletName, entry);
			} catch (Exception e)
			{
				throw new ParseException("Error creating bindlet instance for '" + bindletClassName
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
	private Map<String,IContext<?,?>> parseContexts( Element parent,
		Map<String, BindletEntry> bindletEntries ) throws ParseException
	{
		if (!isTag(parent, XMLDomainTags.CONTEXTS)) throw new ParseException("Invalid tag name");

		Map<String,IContext<?,?>> output = new HashMap<String,IBindletContext<?,?>>();
		NodeList list = getElementsByTag(parent, XMLDomainTags.CONTEXT);

		// for each bindlet context...
		for (int i = 0; i < list.getLength(); ++i)
		{
			Element element = (Element) list.item(i);
			Class<?> contextClass = null;

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
				// create and fill a new bindlet context configuration
				ContextConfig config = new ContextConfig(contextName, contextClassName);
				for (IParameter current : params)
					config.setInitParameter(current.getName(), current.getValue());

				// create a new bindlet context instance
				Constructor<?> ctor = contextClass.getConstructor(IContext.CONSTRUCTOR_ARGS);
				IContext<?, ?> context = (IContext<?, ?>) ctor.newInstance(config);

				// found and register the associated bindlets
				temp = getElementByTag(element, XMLDomainTags.BINDLETS);
				List<String> bindletNames = parseAssociatedBindlets(temp);
				registerBindlets(bindletNames, bindletEntries, context);
								
				output.put(contextName, context);
			} catch (Exception e)
			{
				throw new ParseException("Error creating bindlet instance for '" + contextClassName
					+ "'", e);
			}
		}
		return output;
	}

	/**
	 * Return a list containing the names of all associated bindlets.
	 * 
	 * @param parent
	 * @return
	 * @throws ParseException
	 */
	protected List<String> parseAssociatedBindlets( Element parent ) throws ParseException
	{
		if (!isTag(parent, XMLDomainTags.BINDLETS)) throw new ParseException("Invalid tag name");

		List<String> output = new LinkedList<String>();
		NodeList list = getElementsByTag(parent, XMLDomainTags.ADD_BINDLET);

		// for each associated bindlet context...
		for (int i = 0; i < list.getLength(); ++i)
		{
			Element element = (Element) list.item(i);
			String name = element.getTextContent();
			if (name != null && !name.isEmpty()) output.add(name);
		}

		return output;
	}

	protected List<IBindlet<?, ?>> registerBindlets( List<String> bindletNames,
		Map<String, BindletEntry> bindletInfos, IContext<?, ?> context )
		throws ParseException
	{
		List<IBindlet<?, ?>> output = new LinkedList<IBindlet<?, ?>>();

		for (String currentName : bindletNames)
		{
			BindletEntry entry = bindletInfos.get(currentName);
			if (entry == null) throw new ParseException("The referenced bindlet does not exist: " + currentName);
			
			try
			{
				// complete the bindlet configuration
				BindletConfig config = entry.getConfig();
				config.setBindletContext(context);
				// create a new bindlet instance
				Constructor<?> ctor = entry.getBindletClass().getConstructor(IBindlet.CONSTRUCTOR_ARGS);
				IBindlet<?, ?> bindlet = (IBindlet<?, ?>) ctor.newInstance(config);

				output.add(bindlet);
			} catch (Exception e)
			{
				throw new ParseException("Error creating bindlet instance for '" + entry.getBindletClass().getName()
					+ "'", e);
			}
		}

		return output;
	}

	/*
	 * private List<IBindlet<?,?>> parseBindlets( Element parent ) throws ParseException { if
	 * (!isTag(parent, XMLDomainTags.BINDLETS)) throw new ParseException("Invalid tag name");
	 * 
	 * List<IBindlet<?,?>> output = new LinkedList<IBindlet<?,?>>(); NodeList list =
	 * getElementsByTag(parent, XMLDomainTags.BINDLET);
	 * 
	 * for (int i = 0; i < list.getLength(); ++i) { Element element = (Element)list.item(i);
	 * Class<?> bindletClass = null; IBindletContext<?,?> context = null;
	 * 
	 * // get the bindlet name Element temp = getElementByTag(element, XMLDomainTags.BINDLET_NAME);
	 * if (temp == null) throw new ParseException("The '" + XMLDomainTags.BINDLET_NAME +
	 * "' tag is required"); String bindletName = temp.getTextContent();
	 * 
	 * // get the bindlet class name temp = getElementByTag(element, XMLDomainTags.BINDLET_CLASS);
	 * if (temp == null) throw new ParseException("The '" + XMLDomainTags.BINDLET_CLASS +
	 * "' tag is required"); String bindletClassName = temp.getTextContent();
	 * 
	 * // create the registred init parameters for this bindlet temp = getElementByTag(element,
	 * XMLDomainTags.PARAMETERS); List<IParameter> params = parseParameters(temp);
	 * 
	 * try { // get the bindlet class bindletClass = Class.forName(bindletClassName); // get the
	 * bindlet context class BindletContextInfo info =
	 * bindletClass.getAnnotation(BindletContextInfo.class); if (info == null) throw new
	 * InvalidClassException("The bindlet class must be have the 'BindletContextInfo' annotation");
	 * Class<?> contextClass = info.contextClass(); // check if we already have a compatible context
	 * instance for ( IBindletContext<?,?> current : output ) if (current.getClass() ==
	 * contextClass) context = current; if (context == null) { // create and fill the bindlet
	 * context configuration BindletContextConfig config = new BindletContextConfig(
	 * contextClass.getName() ); //for ( IParameter current : contextParams ) //
	 * config.setBindletParameter( current.getName(), current.getValue() ); // create a new bindlet
	 * context Constructor<?> ctor = contextClass.getConstructor(CONTEXT_CONSTRUCTOR); context =
	 * (IBindletContext<?, ?>)ctor.newInstance(config); output.add(context); } } catch (Exception e)
	 * { throw new ParseException("Error creating bindlet context instance for '" + bindletClassName
	 * + "'", e); }
	 * 
	 * try { // create and fill a new bindlet configuration BindletConfig config = new
	 * BindletConfig(bindletName, context); for ( IParameter current : params )
	 * config.setBindletParameter( current.getName(), current.getValue() );
	 * 
	 * // create a new bindlet instance Constructor<?> ctor =
	 * bindletClass.getConstructor(BINDLET_CONSTRUCTOR); IBindlet<?,?> bindlet =
	 * (IBindlet<?,?>)ctor.newInstance(config); // check if is necessery add the new bindlet in
	 * context if (!(bindlet instanceof Bindlet)) context.addGenericBindlet(bindlet); } catch
	 * (Exception e) { throw new ParseException("Error creating bindlet instance for '" +
	 * bindletClassName + "'", e); } } return output; }
	 */

	public Class<?> getBindletParameter( Class<?> clazz, int index )
	{
		ParameterizedType type = (ParameterizedType) RestProtocol.class.getGenericSuperclass();
		Type param = type.getActualTypeArguments()[index];
		if (param instanceof Class<?>) return (Class<?>) param;
		return null;
	}

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

	/*
	 * public void startElement( String uri, String localName, String qName, Attributes attributes )
	 * throws SAXException { ignoreText = true;
	 * 
	 * XMLDomainTags tag = XMLDomainTags.valueOfTag(qName);
	 * 
	 * switch (tag) { case DOMAIN: domain = new Domain("default"); break; case SERVER: currentServer
	 * = new ServerEntry(); break; case SERVICE: currentService = new ServiceEntry(); break; case
	 * CONNECTOR: currentConnector = new ConnectorEntry(); break; case SERVER_NAME: case
	 * SERVER_CLASS: case CONNECTOR_NAME: case CONNECTOR_CLASS: case SERVICE_NAME: case
	 * SERVICE_CLASS: ignoreText = false; break; } }
	 * 
	 * public void characters( char[] ch, int start, int length ) throws SAXException { if
	 * (ignoreText || start == length) currentText = ""; else currentText = new String(ch, start,
	 * length); }
	 * 
	 * public void endElement( String uri, String localName, String qName ) throws SAXException {
	 * XMLDomainTags tag = XMLDomainTags.valueOfTag(qName);
	 * 
	 * switch (tag) { case SERVER_NAME: currentServer.setName(currentText); break; case
	 * SERVER_CLASS: currentServer.setClassName(currentText); break; case SERVICE_NAME:
	 * currentService.setName(currentText); break; case SERVICE_CLASS:
	 * currentService.setClassName(currentText); break; case CONNECTOR_NAME:
	 * currentConnector.setName(currentText); break; case CONNECTOR_CLASS:
	 * currentConnector.setClassName(currentText); break; case CONNECTOR_ADDRESS:
	 * currentConnector.setAddress(currentText); break; case SERVICE: try { Class<?> serviceClass =
	 * Class.forName( currentService.getClassName() ); Constructor<?> ctor =
	 * serviceClass.getConstructor(SERVICE_CONSTRUCTOR); IService service =
	 * (IService)ctor.newInstance( currentService.getName() ); serviceList.add(service);
	 * 
	 * } catch (Exception e) { // TODO Auto-generated catch block e.printStackTrace(); } break; case
	 * SERVER: serverList.add( createServer(currentServer, serviceList) ); break; case CONNECTOR:
	 * serviceList.add( createService(currentService, contextList) ); break; } }
	 * 
	 * protected IService createService( ServiceEntry entry, List<IBindletContext<?,?>> contexts )
	 * throws SAXException { IService service;
	 * 
	 * try { // create a new server instance Class<?> serverClass = Class.forName(
	 * entry.getClassName() ); Constructor<?> ctor =
	 * serverClass.getConstructor(SERVICE_CONSTRUCTOR); service = (IService)ctor.newInstance(
	 * entry.getName() ); // add all registred services on the server instance for (
	 * IBindletContext<?,?> current : contexts ) service.addContext(current); } catch (Exception e)
	 * { throw new SAXException("Error creating service instance for '" + entry.getName() + "'", e);
	 * } return service; }
	 * 
	 * 
	 * 
	 * public class ServerEntry {
	 * 
	 * private String name;
	 * 
	 * private String className;
	 * 
	 * public void setName( String name ) { this.name = name; }
	 * 
	 * public String getName() { return name; }
	 * 
	 * public void setClassName( String className ) { this.className = className; }
	 * 
	 * public String getClassName() { return className; }
	 * 
	 * }
	 * 
	 * public class ServiceEntry extends ServerEntry {
	 * 
	 * }
	 * 
	 * public class ConnectorEntry extends ServerEntry {
	 * 
	 * private String address;
	 * 
	 * public void setAddress( String address ) { this.address = address; }
	 * 
	 * public String getAddress() { return address; }
	 * 
	 * }
	 */

	public final class BindletEntry
	{

		private String bindletName;

		private Class<?> bindletClass;

		private BindletConfig config;

		public BindletEntry( String bindletName, Class<?> bindletClass, BindletConfig config )
		{
			this.bindletName = bindletName;
			this.bindletClass = bindletClass;
			this.config = config;
		}

		public void setConfig( BindletConfig config )
		{
			this.config = config;
		}

		public BindletConfig getConfig()
		{
			return config;
		}

		public void setBindletName( String bindletName )
		{
			this.bindletName = bindletName;
		}

		public String getBindletName()
		{
			return bindletName;
		}

		public void setBindletClass( Class<?> bindletClass )
		{
			this.bindletClass = bindletClass;
		}

		public Class<?> getBindletClass()
		{
			return bindletClass;
		}

	}
}
