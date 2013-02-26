package corinna.soap.core;


import java.net.URL;

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

	public static final String METHOD_RESPONSE_SUFFIX = "Response";

	public static final String DEFAULT_SCHEMA = "service.xsd";

	public static final String DEFAULT_WSDL = "service.wsdl";

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
			context.wsdlNamespace = context.endpointUrl + classDesc.getName() + "/WSDL";
		if (schemaNamespace == null)
			context.schemaNamespace = context.endpointUrl + classDesc.getName() + "/XSD";
		
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
		wsdlDef.addNamespace("soap", SOAPConstants.NS_URI_SOAP);
		wsdlDef.addNamespace("types", context.schemaNamespace);
		wsdlDef.addNamespace("lns", context.wsdlNamespace);
		wsdlDef.addNamespace("xsd", SchemaConstants.NS_URI_XSD_2001);

		context.wsdlDef = wsdlDef;
		
		generateWsdlTypes(context);
		generateWsdlMethods(context, context.classDesc);
		generateWsdlService(context);

		return wsdlDef;
	}

	protected void generateWsdlTypes( WsdlContext context ) throws Exception
	{
		SchemaGenerator ps = new SchemaGenerator();
		Element root = ps.generateSchema(context.classDesc, context.schemaNamespace);

		Schema schema = new SchemaImpl();
		schema.setElement(root);
		schema.setElementType(SchemaConstants.Q_ELEM_XSD_2001);

		Types types = context.wsdlDef.createTypes();
		types.addExtensibilityElement(schema);
		context.wsdlDef.setTypes(types);
	}

	protected void generateWsdlMethods( WsdlContext context, ClassDescriptor classDesc )
		throws WSDLException
	{
		if (classDesc == null)
			throw new NullPointerException("The class descriptor can not be null");

		for (MethodDescriptor current : classDesc.getMethods())
			generateMethod(context, current);
	}

	protected void generateMethod( WsdlContext context, MethodDescriptor methodDesc )
		throws WSDLException
	{
		if (context == null)
			throw new IllegalArgumentException("The WSDL context can not be null");
		if (methodDesc == null)
			throw new IllegalArgumentException("The method descriptor can not be null");

		String serviceName = context.classDesc.getName() + "Service";
		String portTypeName = serviceName + "PortType";
		String methodName = methodDesc.getName();
		String bindingName = serviceName + "Binding";

		// create the input and output messages
		Message inputMessage = createMessage(context, methodDesc.getName() + "Input");
		Message outputMessage = createMessage(context, methodDesc.getName() + "Output");
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

		// create the operation documentation
		Element docRoot = document.createElementNS("http://schemas.xmlsoap.org/wsdl/", "documentation");
		docRoot.setPrefix("wsdl");
		docRoot.setTextContent("asd" + methodDesc.getDescription());
		// create the operation
		Operation operation = createOperation(context, methodName, inputMessage, outputMessage);
		operation.setDocumentationElement(docRoot);
		context.portType.addOperation(operation);

		// check if the binding is already created
		if (context.binding == null)
		{
			// create the SOAP binding
			SOAPBinding soapBinding = new SOAPBindingImpl();
			soapBinding.setStyle("document");
			soapBinding.setTransportURI("http://schemas.xmlsoap.org/soap/http");

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
		part.setElementName(new QName(context.schemaNamespace, messageName + "Type"));

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
		String serviceName = context.classDesc.getName() + "Service";
		String bindingName = serviceName + "Binding";

		SOAPAddress soapAd = new SOAPAddressImpl();
		soapAd.setLocationURI(context.endpointUrl);

		Port port = context.wsdlDef.createPort();
		port.setBinding(context.wsdlDef.getBinding(new QName(context.wsdlNamespace, bindingName)));
		port.addExtensibilityElement(soapAd);
		port.setName(serviceName + "Port");

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
	
	public static String getXMLSchemaNamespace( Class<?> classRef )
	{
		if (classRef == null) return null;
		return classRef.getName() + "/XSD";
	}
	
	public static String getWSDLNamespace( Class<?> classRef )
	{
		if (classRef == null) return null;
		return classRef.getName() + "/WSDL";
	}
	
}
