package corinna.soap.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.wsdl.extensions.schema.SchemaConstants;

import corinna.service.rpc.ClassDescriptor;
import corinna.service.rpc.MethodDescriptor;
import corinna.service.rpc.MultipleReturnValue;
import corinna.service.rpc.ParameterDescriptor;


public class SchemaGenerator
{

	private static final Class<?>[] TYPE_CLASSES = { Integer.class, Float.class, Long.class,
		String.class, Double.class, Boolean.class, MultipleReturnValue.class, Byte.class,
		Short.class, int.class, float.class, long.class, double.class, boolean.class, 
		byte.class, short.class, URL.class, Date.class, Calendar.class };

	private static final String TYPE_NAMES[] = { "int", "float", "long", "string", 
		"double", "boolean", "string", "byte", "short", "int", "float", 
		"long", "double", "boolean", "byte", "short", "string", "datetime", "datetime" };
	
	private Document document;
	
	public SchemaGenerator() throws Exception 
	{
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		document = docBuilder.newDocument();
	}
	
	public Element generateSchema( ClassDescriptor classDesc, String targetNamespace ) throws Exception
	{
        // create a schema context with all informations needed to generate the XML Schema
		SchemaContext context = new SchemaContext();
		context.targetNamespace = targetNamespace;
		context.classRef = classDesc.getType();
		context.classDesc  = classDesc;
        
		Element root = createElement(context, SchemaConstants.ELEM_SCHEMA);
        root.setAttribute("targetNamespace", targetNamespace);
		context.schemaElement = root;
        
        MethodDescriptor methods[] = classDesc.getMethods();
        
        for (MethodDescriptor method : methods)
        	generateMethod(context, root, method);
        
        return root;
	}
	
	protected Element createElement( SchemaContext context, Element parent, String name )
	{
		if (name == null)
			throw new IllegalArgumentException("The element name can not be null");
		
		Element element = document.createElementNS(context.xmlSchemaNamespace , name);
		element.setPrefix("xsd");
		if (parent != null) parent.appendChild(element);
		return element;
	}
	
	protected Element createElement( SchemaContext context, String name )
	{
		return createElement(context, null, name);
	}
	
	protected boolean isPrimitive( Class<?> classRef )
	{
		int k = 0;
		
		if (classRef.isPrimitive()) return true;
		
		for (k = 0; k < TYPE_NAMES.length && !TYPE_CLASSES[k].equals(classRef); ++k);
		return (k < TYPE_NAMES.length);
	}
	
	protected boolean isEnumeration( Class<?> classRef )
	{
		return classRef.isEnum();
	}
	
	protected void generatePrimitive( SchemaContext context, Element parent, String name, Class<?> classRef )
	{
		Element element = createElement(context, parent, "element");
		element.setAttribute("name", name);
		element.setAttribute("type", "xsd:" + mapToSchemaType(classRef));
	}
	
	protected void generateMethod( SchemaContext context, Element schemaElement, MethodDescriptor method )
	{
		Element element = createElement(context, schemaElement, "element");
		element.setAttribute("name", method.getName() + "InputType");
		element = createElement(context, element, "complexType");
		Element sequence = createElement(context, element, "sequence");
		
		for (int c = 0; c < method.getParameterCount(); ++c)
		{
			ParameterDescriptor param = method.getParameter(c);
			if (!param.isPublic()) continue;
			
			Class<?> fieldType = param.getType();
			
			if (isPrimitive(fieldType))
				generatePrimitive(context, sequence, param.getName(), fieldType);
			else
			{
				String typeName = null;
				if (isEnumeration(fieldType))
					typeName = generateEnum(context, fieldType);
				else
					typeName = generateBean(context, fieldType);
				element = createElement(context, sequence, "element");
				element.setAttribute("name", param.getName());
				element.setAttribute("type", typeName);			
			}
		}
	}
	
	private String mapToSchemaType( Class<?> type )
	{
		int k;

		for (k = 0; k < TYPE_NAMES.length && !TYPE_CLASSES[k].equals(type); ++k);

		if (k < TYPE_NAMES.length)
			return TYPE_NAMES[k];
		else
			return null;
	}
	
	protected String generateBean( SchemaContext context, Class<?> classRef )
	{
		if (classRef == null)
			throw new IllegalArgumentException("The schema element can not be null");
		if (context == null)
			throw new IllegalArgumentException("The schema context can not be null");

		Map<String,Class<?>> fields = extractBeanFields(classRef);
		
		Element element = createElement(context, context.schemaElement, "element");
		element.setAttribute("name", classRef.getName() + "Type");
		element = createElement(context, element, "complexType");
		element = createElement(context, element, "sequence");
		
		for (Map.Entry<String,Class<?>> current : fields.entrySet())
		{
			String typeName = null;
			Class<?> type = current.getValue();
			
			// check if the field type is a enumeration
			if (isPrimitive(type))
				typeName = mapToSchemaType(type);
			else
			if (isEnumeration(type))
				typeName = generateEnum(context, type);
			else
				typeName = generateBean(context, type);
				
			Element temp = createElement(context, element, "element");
			temp.setAttributeNS(SchemaConstants.NS_URI_XSD_2001, "name", current.getKey());
			temp.setAttributeNS(SchemaConstants.NS_URI_XSD_2001, "type", typeName);
		}
		
		return classRef.getName() + "Type";
	}
	
	/**
	 * Returns the XML Schema type name for specified class. If the type is not primitive, a 
	 * new type representing the class will be created in the schema.
	 * 
	 * @param schemaElement
	 * @param type
	 * @return
	 */
	protected String getTypeName( SchemaContext context, Class<?> type )
	{
		if (isPrimitive(type))
			return mapToSchemaType(type);
		else
		if (isEnumeration(type))
			return generateEnum(context, type);
		else
			return generateBean(context, type);
	}
	
	protected String generateEnum( SchemaContext context, Class<?> classRef )
	{
		if (classRef == null)
			throw new IllegalArgumentException("The schema element can not be null");
		if (context == null)
			throw new IllegalArgumentException("The schema context can not be null");
		
		String typeName = classRef.getName() + "Type";
		Object values[] = classRef.getEnumConstants();
		
		Element element = createElement(context, context.schemaElement, "element");
		element.setAttribute("name", typeName);
		element = createElement(context, element, "simpleType");
		element = createElement(context, element, "restriction");
		element.setAttribute("base", mapToSchemaType(String.class));
		
		for (Object current : values)
		{
			Element temp = createElement(context, element, "enumeration");
			temp.setAttributeNS(SchemaConstants.NS_URI_XSD_2001, "value", current.toString());
		}
		
		return typeName;
	}
	
	protected Map<String,Class<?>> extractBeanFields( Class<?> classRef )
	{
		Map<String, Class<?>> output = new HashMap<String, Class<?>>();
				
		Method[] methods = classRef.getMethods();
		
		for (Field field : classRef.getDeclaredFields())
		{
			String fieldName = null;
			
			corinna.service.rpc.annotation.Field annot = field.getAnnotation(corinna.service.rpc.annotation.Field.class);
			if (annot != null) fieldName = annot.name();
			if (fieldName == null || fieldName.isEmpty()) fieldName = field.getName();
			
			// check if the class has a getter and setter for this field
			boolean setter = containsBeanMethod(methods, fieldName, MethodPrefix.SET);
			if (!setter) continue;
			
			boolean getter = false;
			if (field.getType() == Boolean.class || field.getType() == boolean.class)
				getter = containsBeanMethod(methods, fieldName, MethodPrefix.IS);
			if (!getter)
				getter = containsBeanMethod(methods, fieldName, MethodPrefix.GET);
			if (!getter) continue;
			
			output.put(fieldName, field.getType());
		}
		
		return output;
	}
	
	protected boolean containsBeanMethod( Method[] methods, String fieldname, MethodPrefix prefix )
	{
		if (fieldname == null || fieldname.isEmpty())
			throw new IllegalAccessError("The method name can not be null or empty");
		
		// ensure that the first character in field name is captalized
		char newName[] = fieldname.toCharArray();
		if (Character.isLowerCase( newName[0] ) )
			newName[0] = Character.toUpperCase( newName[0] );

		fieldname = prefix.getPrefix() + String.valueOf(newName);
		
		for (Method method : methods)
			if (method.getName().equals(fieldname)) return true;//method;
		return false;//null;
	}
	
	public class SchemaContext
	{
		
		public ClassDescriptor classDesc = null;

		public String xmlSchemaNamespace = SchemaConstants.NS_URI_XSD_2001;

		public Element schemaElement = null;

		public Class<?> classRef = null;
		
		public String targetNamespace = null;
		
	}
	
	public static enum MethodPrefix
	{
		
		GET("get"),
		
		SET("set"),
		
		IS("is");
		
		private String prefix = null;
		
		private MethodPrefix( String prefix )
		{
			this.prefix = prefix;
		}

		public String getPrefix()
		{
			return prefix;
		}
		
	}
	
}
