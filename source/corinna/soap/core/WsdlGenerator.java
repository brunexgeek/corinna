package corinna.soap.core;


import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.wsdl.extensions.schema.SchemaConstants;
import com.ibm.wsdl.extensions.schema.SchemaImpl;
import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;
import com.ibm.wsdl.extensions.soap.SOAPBodyImpl;
import com.ibm.wsdl.extensions.soap.SOAPConstants;
import com.ibm.wsdl.extensions.soap.SOAPOperationImpl;

import corinna.rpc.ClassDescriptor;
import corinna.rpc.MethodDescriptor;


public class WsdlGenerator
{

	private static final Class<?>[] TYPE_CLASSES = { Integer.class, Float.class, Long.class,
			String.class, Double.class, Boolean.class, Byte.class, Short.class, int.class,
			float.class, long.class, double.class, boolean.class, byte.class, short.class };

	private static final String TYPE_NAMES[] = { "int", "float", "long", "string", "double",
			"boolean", "byte", "short", "int", "float", "long", "double", "boolean", "byte",
			"short" };

	public static final String PARAMETER_RESULT = "result";

	//public static final String SUFFIX_MESSAGE_RESPONSE = "Response";

	public static final String DEFAULT_SCHEMA = "service.xsd";

	public static final String DEFAULT_WSDL = "service.wsdl";

	private static final String SUFFIX_PORTTYPE = "PortType";

	private static final String SUFFIX_BINDING = "Binding";

	private static final String SUFFIX_INPUT = SchemaGenerator.SUFFIX_INPUT;//"Input";

	private static final String SUFFIX_OUTPUT = SchemaGenerator.SUFFIX_OUTPUT;//"Output";
	
	private static final String SUFFIX_MESSAGE = SchemaGenerator.SUFFIX_MESSAGE;
	
	public static final String SUFFIX_INPUT_MESSAGE = SUFFIX_INPUT + SUFFIX_MESSAGE;
	
	public static final String SUFFIX_OUTPUT_MESSAGE = SUFFIX_OUTPUT + SUFFIX_MESSAGE;

	private static final String NAMESPACE_WSDL = SOAPConstants.NS_URI_SOAP;

	private static final String NAMESPACE_XSD = SchemaConstants.NS_URI_XSD_2001;
	
	private static final String NAMESPACE_SOAP = "http://schemas.xmlsoap.org/soap/http";

	private static final String SUFFIX_SERVICE = "Service";

	private static final String PREFIX_SOAP = "soap";

	/**
	 * Prefix for the generated XML Schema.
	 */
	private static final String PREFIX_LOCAL_XSD = "types";

	/**
	 * Prefix for the generated WSDL.
	 */
	private static final String PREFIX_LOCAL_WSDL = "lns";

	private static final String PREFIX_XSD = "xsd";

	private static final String SUFFIX_PORT = "Port";

	private String serviceName = "ServiceName";

	private String serviceDescription = "Service description";

	private WSDLFactory wsdlFactory = null;

	private Document document;

	private WsdlContext context = null;
	
	public WsdlGenerator( ClassDescriptor classDesc, String endpointUrl ) throws WSDLException
	{
		this(classDesc, endpointUrl, null, null);
	}
	
	public WsdlGenerator( ClassDescriptor classDesc, String endpointUrl, String wsdlNamespace, String schemaNamespace ) throws WSDLException
	{
		if (classDesc == null)
			throw new IllegalArgumentException("The class descriptor can not be null");
		
		// initialize internal parameters
		context = new WsdlContext();
		context.wsdlDef = null;
		context.classDesc = classDesc;
		context.wsdlNamespace = wsdlNamespace;
		context.schemaNamespace = schemaNamespace;
		context.extensionReg = new ExtensionRegistry();
		context.endpointUrl = endpointUrl;
		if (!endpointUrl.endsWith("/")) context.endpointUrl = endpointUrl + "/";

		if (wsdlNamespace == null)
			context.wsdlNamespace = getWSDLNamespace(endpointUrl, classDesc);
		if (schemaNamespace == null)
			context.schemaNamespace = getXMLSchemaNamespace(endpointUrl, classDesc);
		
		// used to create the documentation elements
		try
		{
			wsdlFactory = WSDLFactory.newInstance();
			
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			document = docBuilder.newDocument();
		} catch (Exception e)
		{
			throw new WSDLException(WSDLException.CONFIGURATION_ERROR,
				"Error initializing WSDL generator", e);
		}
	}

	public Definition generateWsdl( ) throws Exception
	{
		Definition wsdlDef = wsdlFactory.newDefinition();

		wsdlDef.setTargetNamespace(context.wsdlNamespace);
		wsdlDef.setQName(new QName(context.classDesc.getName()));
		wsdlDef.addNamespace(PREFIX_SOAP, NAMESPACE_WSDL);
		wsdlDef.addNamespace(PREFIX_LOCAL_XSD, context.schemaNamespace);
		wsdlDef.addNamespace(PREFIX_LOCAL_WSDL, context.wsdlNamespace);
		wsdlDef.addNamespace(PREFIX_XSD, NAMESPACE_XSD);

		context.wsdlDef = wsdlDef;
		
		generateWsdlTypes(context);
		generateWsdlMethods(context, context.classDesc);
		generateWsdlService(context);

		return wsdlDef;
	}

	/**
	 * Generate a XML Schema defining all types in the given class descriptor to be used in the WSDL messages.
	 * 
	 * @param context The context information object.
	 * @throws Exception
	 */
	protected void generateWsdlTypes( WsdlContext context ) throws Exception
	{
		SchemaGenerator ps = new SchemaGenerator();
		Element root = ps.generateSchema(context.classDesc, context.schemaNamespace);

		// TODO: try to remove the specified reference for the 'SchemaImpl' from third-part package
		Schema schema = new SchemaImpl();
		schema.setElement(root);
		schema.setElementType(SchemaConstants.Q_ELEM_XSD_2001);

		Types types = context.wsdlDef.createTypes();
		types.addExtensibilityElement(schema);
		context.wsdlDef.setTypes(types);
	}

	/**
	 * Generate all WSDL entries required to export each method in the given class descriptor. For each
	 * method found will be called the {@link #generateMethod} method.
	 * 
	 * @param context The context information object.
	 * @param methodDesc Class descriptor that contains the methods information.
	 * @throws WSDLException whether an exception occurs while generating the method entries on the WSDL.
	 */
	protected void generateWsdlMethods( WsdlContext context, ClassDescriptor classDesc )
		throws WSDLException
	{
		if (classDesc == null)
			throw new NullPointerException("The class descriptor can not be null");

		for (MethodDescriptor current : classDesc.getMethods())
			generateMethod(context, current);
	}

	/**
	 * Generate all WSDL entries required to export the given method. That entries can includes the 
	 * parameters and return value messages, the operation and the operation binding.
	 * 
	 * @param context The context information object.
	 * @param methodDesc Method descriptor that contains the method information.
	 * @throws WSDLException whether an exception occurs while generating the method entries on the WSDL.
	 */
	protected void generateMethod( WsdlContext context, MethodDescriptor methodDesc )
		throws WSDLException
	{
		if (context == null)
			throw new IllegalArgumentException("The WSDL context can not be null");
		if (methodDesc == null)
			throw new IllegalArgumentException("The method descriptor can not be null");

		String serviceName = context.classDesc.getName() + SUFFIX_SERVICE;
		String portTypeName = serviceName + SUFFIX_PORTTYPE;
		String methodName = methodDesc.getName();
		String bindingName = serviceName + SUFFIX_BINDING;

		// create the input and output messages
		Message inputMessage = createMessage(context, methodDesc.getName() + SUFFIX_INPUT);
		Message outputMessage = createMessage(context, methodDesc.getName() + SUFFIX_OUTPUT);
		context.wsdlDef.addMessage(inputMessage);
		context.wsdlDef.addMessage(outputMessage);

		// check if the port type is already created
		if (context.portType == null)
		{
			context.portType = context.wsdlDef.createPortType();
			context.portType.setQName(new QName(context.wsdlNamespace, portTypeName));
			context.portType.setUndefined(false);
			context.wsdlDef.addPortType(context.portType);
		}

		// create the operation
		Operation operation = createOperation(context, methodName, inputMessage, outputMessage);
		// create the operation documentation, if any
		if (methodDesc.getDescription() != null || !methodDesc.getDescription().isEmpty())
		{
			Element docRoot = document.createElementNS(NAMESPACE_WSDL, "documentation");
			docRoot.setPrefix("wsdl");
			docRoot.setTextContent(methodDesc.getDescription());
			operation.setDocumentationElement(docRoot);
		}
		// add the operation to the port type
		context.portType.addOperation(operation);

		// check if the binding is already created
		if (context.binding == null)
		{
			// create the SOAP binding
			SOAPBinding soapBinding = new SOAPBindingImpl();
			soapBinding.setStyle("document");
			soapBinding.setTransportURI(NAMESPACE_SOAP);

			context.binding = context.wsdlDef.createBinding();
			context.binding.setQName(new QName(context.wsdlNamespace, bindingName));
			context.binding.setPortType(context.portType);
			context.binding.setUndefined(false);
			context.binding.addExtensibilityElement(soapBinding);
			context.wsdlDef.addBinding(context.binding);
		}
		// create the operation binding
		BindingOperation bindingOp = createBindingOperation(context, methodName,
			(inputMessage != null), (outputMessage != null));
		context.binding.addBindingOperation(bindingOp);
	}

	protected Message createMessage( WsdlContext context, String name )
	{
		String messageName = name;

		// create the input message
		Part part = context.wsdlDef.createPart();
		part.setName("body");
		part.setElementName(new QName(context.schemaNamespace, messageName + SUFFIX_MESSAGE));

		Message message = context.wsdlDef.createMessage();
		message.addPart(part);
		message.setQName(new QName(context.wsdlNamespace, messageName));
		message.setUndefined(false);

		return message;
	}

	protected Operation createOperation( WsdlContext context, String name, Message inputMessage,
		Message outputMessage )
	{
		Input input = null;
		Output output = null;
		String operationName = name;

		if (inputMessage == null && outputMessage == null) return null;

		// try to create the input
		if (inputMessage != null)
		{
			input = context.wsdlDef.createInput();
			input.setMessage(inputMessage);
		}
		// try to create the output
		if (outputMessage != null)
		{
			output = context.wsdlDef.createOutput();
			output.setMessage(outputMessage);
		}
		// create the operation
		Operation operation = context.wsdlDef.createOperation();
		operation.setName(operationName);
		if (input != null) operation.setInput(input);
		if (output != null) operation.setOutput(output);
		operation.setUndefined(false);

		return operation;
	}

	protected BindingOperation createBindingOperation( WsdlContext context, String name,
		boolean hasInput, boolean hasOutput ) throws WSDLException
	{
		BindingInput input = null;
		BindingOutput output = null;
		String operationName = name;

		if (!hasInput && !hasOutput) return null;

		// try to create the input
		if (hasInput)
		{
			SOAPBody soapBody = new SOAPBodyImpl();
			soapBody.setUse("literal");
			input = context.wsdlDef.createBindingInput();
			input.addExtensibilityElement(soapBody);
		}
		// try to create the output
		if (hasOutput)
		{
			SOAPBody soapBody = new SOAPBodyImpl();
			soapBody.setUse("literal");
			output = context.wsdlDef.createBindingOutput();
			output.addExtensibilityElement(soapBody);
		}
		// create the SOAP operation
		String soapActionUrl = context.endpointUrl + name;
		SOAPOperation soapOp = new SOAPOperationImpl();
		soapOp.setSoapActionURI(soapActionUrl);
		// create the operation
		BindingOperation operation = context.wsdlDef.createBindingOperation();
		operation.setName(operationName);
		operation.addExtensibilityElement(soapOp);
		if (input != null) operation.setBindingInput(input);
		if (output != null) operation.setBindingOutput(output);

		return operation;
	}

	protected void generateWsdlService( WsdlContext context )
	{
		String serviceName = context.classDesc.getName() + SUFFIX_SERVICE;
		String bindingName = serviceName + SUFFIX_BINDING;

		SOAPAddress soapAd = new SOAPAddressImpl();
		soapAd.setLocationURI(context.endpointUrl);

		Port port = context.wsdlDef.createPort();
		port.setBinding(context.wsdlDef.getBinding(new QName(context.wsdlNamespace, bindingName)));
		port.addExtensibilityElement(soapAd);
		port.setName(serviceName + SUFFIX_PORT);

		Service service = context.wsdlDef.createService();
		service.addPort(port);
		service.setQName(new QName(serviceName));

		context.wsdlDef.addService(service);
	}

	public static String getTypeName( Class<?> type )
	{
		int k;

		for (k = 0; k < TYPE_NAMES.length && !TYPE_CLASSES[k].equals(type); ++k);

		if (k < TYPE_NAMES.length)
			return TYPE_NAMES[k];
		else
			return "string";
	}

	public void setServiceName( String serviceName )
	{
		this.serviceName = serviceName;
	}

	public String getServiceName()
	{
		return serviceName;
	}

	public void setServiceDescription( String serviceDescription )
	{
		this.serviceDescription = serviceDescription;
	}

	public String getServiceDescription()
	{
		return serviceDescription;
	}

	protected class WsdlContext
	{

		public ExtensionRegistry extensionReg;

		public String endpointUrl = null;

		public Binding binding;

		public String schemaNamespace = null;

		public String wsdlNamespace = null;

		public ClassDescriptor classDesc = null;

		public Definition wsdlDef = null;

		public PortType portType = null;

	}
	
	public static String getXMLSchemaNamespace( String endpointUrl, ClassDescriptor classDesc )
	{
		if (endpointUrl == null || classDesc == null) return null;
		if (!endpointUrl.endsWith("/")) endpointUrl = endpointUrl + "/";
		return endpointUrl + classDesc.getName() + "/XSD";
	}
	
	public static String getWSDLNamespace( String endpointUrl, ClassDescriptor classDesc )
	{
		if (endpointUrl == null || classDesc == null) return null;
		if (!endpointUrl.endsWith("/")) endpointUrl = endpointUrl + "/";
		return endpointUrl + classDesc.getName() + "/WSDL";
	}
	
}
