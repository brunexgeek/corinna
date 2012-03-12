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

import org.w3c.dom.Element;

import com.ibm.wsdl.extensions.schema.SchemaConstants;
import com.ibm.wsdl.extensions.schema.SchemaImpl;
import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
import com.ibm.wsdl.extensions.soap.SOAPBindingImpl;
import com.ibm.wsdl.extensions.soap.SOAPBodyImpl;
import com.ibm.wsdl.extensions.soap.SOAPConstants;
import com.ibm.wsdl.extensions.soap.SOAPOperationImpl;

import corinna.service.rpc.ClassDescriptor;
import corinna.service.rpc.MethodDescriptor;
import corinna.service.rpc.MultipleReturnValue;


public class WsdlGenerator
{

	private static final Class<?>[] TYPE_CLASSES = { Integer.class, Float.class, Long.class,
		String.class, Double.class, Boolean.class, MultipleReturnValue.class, Byte.class,
		Short.class, int.class, float.class, long.class, double.class, boolean.class, 
		byte.class, short.class };

	private static final String TYPE_NAMES[] = { "int", "float", "long", "string", 
		"double", "boolean", "string", "byte", "short", "int", "float", 
		"long", "double", "boolean", "byte", "short" };

	public static final String PARAMETER_RESULT = "result";
	
	public static final String METHOD_RESPONSE_SUFFIX = "Response";
		
	public static final String DEFAULT_SCHEMA = "service.xsd";
	
	public static final String DEFAULT_WSDL = "service.wsdl";
	
	private URL endpointUrl;
	
	private String serviceName = "ServiceName";
	
	private String serviceDescription = "Service description";
	
	private WSDLFactory wsdlFactory = null;
	
	public WsdlGenerator( ) throws WSDLException
	{
		wsdlFactory = WSDLFactory.newInstance();
	}
	
	public Definition generateWsdl( ClassDescriptor classDesc, String endpointUrl, String wsdlNamespace, 
		String schemaNamespace ) throws Exception
	{
		if (!endpointUrl.endsWith("/")) 
			endpointUrl += "/";
		if (wsdlNamespace == null)
			wsdlNamespace = endpointUrl + classDesc.getSimpleName() + ".wsdl";
		if (schemaNamespace == null)
			schemaNamespace = endpointUrl + classDesc.getSimpleName() + ".xsd";
		
		Definition wsdlDef = wsdlFactory.newDefinition();
		
		wsdlDef.setTargetNamespace(wsdlNamespace);
		wsdlDef.setQName( new QName(classDesc.getSimpleName()));
		wsdlDef.addNamespace("soap", SOAPConstants.NS_URI_SOAP);
		wsdlDef.addNamespace("types", schemaNamespace);
		wsdlDef.addNamespace("lns", wsdlNamespace);
		wsdlDef.addNamespace("xsd", SchemaConstants.NS_URI_XSD_2001);
		
		WsdlContext context = new WsdlContext();
		context.wsdlDef = wsdlDef;
		context.classDesc = classDesc;
		context.wsdlNamespace = wsdlNamespace;
		context.schemaNamespace = schemaNamespace;
		context.endpointUrl = endpointUrl;
		context.extensionReg = new ExtensionRegistry();
		
		generateWsdlTypes(context);
		generateWsdlMethods(context, classDesc);
		generateWsdlService(context);
		
		return wsdlDef;
	}
	
	protected void generateWsdlTypes( WsdlContext context ) throws Exception
	{
		SchemaGenerator ps = new SchemaGenerator();
		Element root = ps.generateSchema(context.classDesc, context.schemaNamespace);
		
        Schema schema = new SchemaImpl();
        schema.setElement(root);
        schema.setElementType( SchemaConstants.Q_ELEM_XSD_2001 );
        
        Types types = context.wsdlDef.createTypes();
		types.addExtensibilityElement(schema);
		context.wsdlDef.setTypes(types);
	}
	
	protected void generateWsdlMethods( WsdlContext context, ClassDescriptor classDesc ) throws WSDLException
	{
		if (classDesc == null)
			throw new NullPointerException("The class descriptor can not be null");
		
		for ( MethodDescriptor current : classDesc.getMethods() )
			generateMethod(context, current);
	}

	protected void generateMethod( WsdlContext context, MethodDescriptor methodDesc ) throws WSDLException
	{
		if (context == null)
			throw new IllegalArgumentException("The WSDL context can not be null");
		if (methodDesc == null)
			throw new IllegalArgumentException("The method descriptor can not be null");
		
		String serviceName = context.classDesc.getSimpleName() + "Service";
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
			context.portType.setQName( new QName(context.wsdlNamespace, portTypeName) );
			context.portType.setUndefined(false);
			context.wsdlDef.addPortType(context.portType);
		}
		// create the operation
		Operation operation = createOperation(context, methodName, inputMessage, outputMessage);
		context.portType.addOperation(operation);
		
		// check if the binding is already created
		if (context.binding == null)
		{
			// create the SOAP binding
			SOAPBinding soapBinding = new SOAPBindingImpl();
			soapBinding.setStyle("document");
			soapBinding.setTransportURI("http://schemas.xmlsoap.org/soap/http");
			
			context.binding = context.wsdlDef.createBinding();
			context.binding.setQName( new QName(context.wsdlNamespace, bindingName) );
			context.binding.setPortType(context.portType);
			context.binding.setUndefined(false);
			context.binding.addExtensibilityElement(soapBinding);
			context.wsdlDef.addBinding(context.binding);
		}
		// create the operation binding
		BindingOperation bindingOp = createBindingOperation(context, methodName, (inputMessage != null), 
			(outputMessage != null));
		context.binding.addBindingOperation(bindingOp);
	}
	
	protected Message createMessage( WsdlContext context, String name )
	{
		String messageName = name;
		
		// create the input message
		Part part = context.wsdlDef.createPart();
		part.setName("body");
		part.setElementName( new QName(context.schemaNamespace, messageName + "Type") );
		
		Message message = context.wsdlDef.createMessage();
		message.addPart(part);
		message.setQName( new QName(context.wsdlNamespace, messageName) );
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
		
		if (!hasInput &&  !hasOutput) return null;
		
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
	
	protected void generateWsdlBinding( StringBuffer buffer, ClassDescriptor classDesc )
	{
		buffer.append("<wsdl:binding name=\"ServiceBinding\" type=\"tns:ServicePortType\">");
		buffer.append("<soap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/>");
		
		for ( MethodDescriptor current : classDesc.getMethods() )
			generateWsdlBindingOperation(buffer, current);
		
		buffer.append("</wsdl:binding>");
	}
	
	protected void generateWsdlBindingOperation( StringBuffer buffer, MethodDescriptor current )
	{
		String methodName = current.getName();

		buffer.append("<wsdl:operation name=\"");
		buffer.append(methodName);
		buffer.append("\"><soap:operation soapAction=\"");
		buffer.append(endpointUrl);
		buffer.append("/");
		buffer.append(methodName);
		buffer.append("\"/><wsdl:input><soap:body use=\"literal\"/></wsdl:input>");
		buffer.append("<wsdl:output><soap:body use=\"literal\"/></wsdl:output>");
		buffer.append("</wsdl:operation>");
	}

	protected void generateWsdlService( WsdlContext context )
	{
		String serviceName = context.classDesc.getSimpleName() + "Service";
		String bindingName = serviceName + "Binding";
		
		SOAPAddress soapAd = new SOAPAddressImpl();
		soapAd.setLocationURI(context.endpointUrl);
		
		Port port = context.wsdlDef.createPort();
		port.setBinding( context.wsdlDef.getBinding( new QName(context.wsdlNamespace, bindingName) ) );
		port.addExtensibilityElement(soapAd);
		port.setName(serviceName + "Port");
		
		Service service = context.wsdlDef.createService();
		service.addPort(port);
		service.setQName( new QName(serviceName) );
		
		context.wsdlDef.addService(service);
		
		/*port.s
		Service service = context.wsdlDef.createService();
		service.
		buffer.append("<wsdl:service name=\"");
		buffer.append(serviceName);
		buffer.append("\"><wsdl:documentation>");
		buffer.append(serviceDescription);
		buffer.append("</wsdl:documentation>");
		buffer.append("<wsdl:port name=\"ServicePort\" binding=\"tns:ServiceBinding\">");
		buffer.append("<soap:address location=\"");
		buffer.append(endpointUrl);
		buffer.append("\"/></wsdl:port></wsdl:service>");*/
	}
	
	public static String getTypeName( Class<?> type )
	{
		int k;

		for (k = 0; k < TYPE_NAMES.length && !TYPE_CLASSES[k].equals(type); ++k)
			;

		if (k < TYPE_NAMES.length)
			return TYPE_NAMES[k];
		else
			return "string";
	}

	public void setEndpointUrl( URL endpointUrl )
	{
		this.endpointUrl = endpointUrl;
	}

	public URL getEndpointUrl()
	{
		return endpointUrl;
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
	
	public class WsdlContext
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
	
}
