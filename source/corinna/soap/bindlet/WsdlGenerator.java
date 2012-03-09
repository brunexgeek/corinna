package corinna.soap.bindlet;

import java.net.MalformedURLException;
import java.net.URL;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;

import corinna.service.rpc.ClassDescriptor;
import corinna.service.rpc.MethodDescriptor;
import corinna.service.rpc.MultipleReturnValue;
import corinna.service.rpc.ParameterDescriptor;

// TODO: move to 'corinna.soap.core'
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
	
	public WsdlGenerator( String endpointUrl ) throws MalformedURLException
	{
		this.endpointUrl = new URL(endpointUrl);
	}
	
	public WsdlGenerator( URL endpointUrl )
	{
		this.endpointUrl = endpointUrl;
	}
		
	protected void generateWsdlTypes( StringBuffer buffer, ClassDescriptor clazz )
	{
		if (clazz == null)
			throw new NullPointerException("The class descriptor can not be null");
		
		buffer.append("<wsdl:types><schema elementFormDefault=\"qualified\" targetNamespace=\"");
		buffer.append(endpointUrl);
		buffer.append("/");
		buffer.append(DEFAULT_SCHEMA);
		buffer.append("\" xmlns=\"http://www.w3.org/2001/XMLSchema\">");
		
		for ( MethodDescriptor current : clazz.getMethods() )
			generateWsdlTypes(buffer, current);

		buffer.append("</schema></wsdl:types>");
	}
	
	protected void generateWsdlTypes( StringBuffer buffer, MethodDescriptor method )
	{
		if (method == null)
			throw new NullPointerException("The method descriptor can not be null");
		
		buffer.append("<element name=\"");
		buffer.append(method.getName());
		buffer.append("\"><complexType><sequence>");
		for (int c = 0; c < method.getParameterCount(); ++c)
		{
			ParameterDescriptor param = method.getParameter(c);
			if (!param.isPublic()) continue;
			buffer.append("<element name=\"");
			buffer.append( param.getName() );
			buffer.append("\" type=\"");
			buffer.append( getTypeName( param.getType() ) );
			buffer.append("\"/>");
		}
		buffer.append("</sequence></complexType></element>");
		
		// generate the type for method response
		buffer.append("<element name=\"");
		buffer.append(method.getName());
		buffer.append("Response\"><complexType><sequence>");
		buffer.append("<element name=\"result\" type=\"");
		buffer.append( getTypeName( method.getReturnType() ) );
		buffer.append("\"/></sequence></complexType></element>");
	}

	public String generateWsdl( ClassDescriptor classDesc )
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<wsdl:definitions name=\"ServiceName\" targetNamespace=\"");
		sb.append(endpointUrl);
		sb.append("/?wsdl\"  xmlns:tns=\"");
		sb.append(endpointUrl);
		sb.append("/?wsdl\"  xmlns:types=\"");
		sb.append(endpointUrl);
		sb.append("/");
		sb.append(DEFAULT_SCHEMA);
		sb.append("\"  xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\"");
        sb.append("  xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\">");
		generateWsdlTypes(sb, classDesc);
		generateWsdlMessages(sb, classDesc);
		generateWsdlPort(sb, classDesc);
		generateWsdlBinding(sb, classDesc);
		generateWsdlService(sb);
		sb.append("</wsdl:definitions>");
		
		return sb.toString();
	}
	
	protected void generateWsdlMessages( StringBuffer buffer, ClassDescriptor classDesc )
	{
		if (classDesc == null)
			throw new NullPointerException("The class descriptor can not be null");
		
		for ( MethodDescriptor current : classDesc.getMethods() )
			generateWsdlMessage(buffer, current);
	}
	
	protected void generateWsdlPort( StringBuffer buffer, ClassDescriptor classDesc )
	{
		buffer.append("<wsdl:portType name=\"ServicePortType\">");
		
		for ( MethodDescriptor current : classDesc.getMethods() )
			generateWsdlOperation(buffer, current);
		
		buffer.append("</wsdl:portType>");
	}
	
	protected void generateWsdlOperation( StringBuffer buffer, MethodDescriptor current )
	{
		String methodName = current.getName();

		buffer.append("<wsdl:operation name=\"");
		buffer.append(methodName);
		buffer.append("\"><wsdl:input message=\"tns:");
		buffer.append(methodName);
		buffer.append("Input\"/><wsdl:output message=\"tns:");
		buffer.append(methodName);
		buffer.append("Output\"/></wsdl:operation>");
	}

	protected void generateWsdlMessage( StringBuffer buffer, MethodDescriptor methodDesc )
	{
		if (methodDesc == null)
			throw new NullPointerException("The method descriptor can not be null");
		
		// generate the input message definition
		buffer.append("<wsdl:message name=\"");
		buffer.append(methodDesc.getName());
		buffer.append("Input\"><wsdl:part name=\"body\" element=\"types:");
		buffer.append(methodDesc.getName());
		buffer.append("\"/></wsdl:message>");
		
		// generate the output message definition
		buffer.append("<wsdl:message name=\"");
		buffer.append(methodDesc.getName());
		buffer.append("Output\"><wsdl:part name=\"body\" element=\"types:");
		buffer.append(methodDesc.getName());
		buffer.append("Response\"/></wsdl:message>");
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

	protected void generateWsdlService( StringBuffer buffer )
	{
		buffer.append("<wsdl:service name=\"");
		buffer.append(serviceName);
		buffer.append("\"><wsdl:documentation>");
		buffer.append(serviceDescription);
		buffer.append("</wsdl:documentation>");
		buffer.append("<wsdl:port name=\"ServicePort\" binding=\"tns:ServiceBinding\">");
		buffer.append("<soap:address location=\"");
		buffer.append(endpointUrl);
		buffer.append("\"/></wsdl:port></wsdl:service>");
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
	
}
