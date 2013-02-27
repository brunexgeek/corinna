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

import corinna.rpc.ClassDescriptor;
import corinna.rpc.MethodDescriptor;
import corinna.rpc.ParameterDescriptor;


public class SchemaGenerator
{

	private static final Class<?>[] TYPE_CLASSES = { Integer.class, Float.class, Long.class,
		String.class, Double.class, Boolean.class, Byte.class,
		Short.class, int.class, float.class, long.class, double.class, boolean.class, 
		byte.class, short.class, URL.class, Date.class, Calendar.class };

	private static final String TYPE_NAMES[] = { "integer", "float", "long", "string", 
		"double", "boolean", "byte", "short", "integer", "float", 
		"long", "double", "boolean", "byte", "short", "string", "datetime", "datetime" };

	private static final String PREFIX_XMLSCHEMA = "xsd";

	private static final String PREFIX_TYPES = "types";

	public static final String SUFFIX_TYPE = "Type";
	
	public static final String SUFFIX_MESSAGE = "Message";

	public static final String SUFFIX_OUTPUT = "Output";

	public static final String SUFFIX_INPUT = "Input";
	
	private Document document;
	
	private Map<Class<?>, String> types = null;
	
	public SchemaGenerator() throws Exception 
	{
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		document = docBuilder.newDocument();
		types = new HashMap<Class<?>, String>();
	}
	
	/**
	 * Generate an XML Schema that provide the methods from the specified class.
	 * 
	 * @param classDesc
	 * @param targetNamespace
	 * @return
	 * @throws Exception
	 */
	public Element generateSchema( ClassDescriptor classDesc, String targetNamespace ) throws Exception
	{
        // create a schema context with all informations needed to generate the XML Schema
		SchemaContext context = new SchemaContext();
		context.targetNamespace = targetNamespace;
		context.classRef = classDesc.getType();
		context.classDesc  = classDesc;
        
		Element root = createElement(context, null, SchemaConstants.ELEM_SCHEMA);
        root.setAttribute("targetNamespace", targetNamespace);
        root.setAttribute("xmlns:" + PREFIX_TYPES, targetNamespace);
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
		element.setPrefix(PREFIX_XMLSCHEMA);
		if (parent != null) parent.appendChild(element);
		return element;
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
	
	/**
	 * Generate all XML Schema entries required to export the given method. That entries can includes the 
	 * parameters and return value types.
	 * 
	 * @param context
	 * @param schemaElement
	 * @param method
	 */
	protected void generateMethod( SchemaContext context, Element schemaElement, MethodDescriptor method )
	{
		// construct the XML schema type for each method parameter
		Element element = createElement(context, schemaElement, "element");
		element.setAttribute("name", method.getName() + SUFFIX_INPUT + SUFFIX_MESSAGE);
		element = createElement(context, element, "complexType");
		Element sequence = createElement(context, element, "sequence");
		
		for (int c = 0; c < method.getParameterCount(); ++c)
		{
			ParameterDescriptor param = method.getParameter(c);
			if (!param.isPublic()) continue;
			
			createElement(context, sequence, param.getName(), param.getType(), 
				param.isRequired());
		}
		
		// construct the XML schema type for the return value
		element = createElement(context, schemaElement, "element");
		element.setAttribute("name", method.getName() + SUFFIX_OUTPUT + SUFFIX_MESSAGE);
		element = createElement(context, element, "complexType");
		sequence = createElement(context, element, "sequence");
		createElement(context, sequence, "result", method.getReturnType(), true);
	}
	
	/**
	 * Create a XML Schema 'element' for the given type. If necessary, a XML complex type will be
	 * created to store all type data.
	 * 
	 * @param context
	 * @param parent
	 * @param name
	 * @param type
	 * @param isRequired
	 * @return
	 */
	private Element createElement( SchemaContext context, Element parent, String name,
		Class<?> type, boolean isRequired )
	{
		Element element;

		String typeName = getTypeName(context, type);		

		// create the element referencing the generated/infered type
		element = createElement(context, parent, "element");
		element.setAttribute("type", typeName);
		element.setAttribute("nillable", isRequired? "false" : "true");
		element.setAttribute("name", name);
		
		return element;
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
	
	protected String generateBeanType( SchemaContext context, String name, Class<?> classRef )
	{
		if (classRef == null)
			throw new IllegalArgumentException("The schema element can not be null");
		if (context == null)
			throw new IllegalArgumentException("The schema context can not be null");

		// check whether the given type is already generated
		String pojoName = types.get(classRef);
		if (pojoName != null) return pojoName;
		
		Map<String,Class<?>> fields = extractBeanFields(classRef);
		pojoName = PREFIX_TYPES + ":" + classRef.getSimpleName() + SUFFIX_TYPE;
		
		// register the new type (insert before complete the job to avoid infinite recursion)
		types.put(classRef, pojoName);
		
		//Element element = createElement(context, context.schemaElement, "element");
		//element.setAttribute("name", pojoName);
		//element = createElement(context, element, "complexType");
		Element element = createElement(context, context.schemaElement, "complexType");
		element.setAttribute("name", pojoName);
		element = createElement(context, element, "sequence");
		
		for (Map.Entry<String,Class<?>> current : fields.entrySet())
		{
			String typeName = null;
			Class<?> type = current.getValue();
			boolean isPOJO = false;
			
			// check if the field type is a enumeration
			if (isPrimitive(type))
				typeName = PREFIX_XMLSCHEMA + ":" + mapToSchemaType(type);
			else
			if (isEnumeration(type))
				typeName = generateEnumType(context, null, type);
			else
			{
				typeName = generateBeanType(context, null, type);
				isPOJO = true;
			}

			Element temp = createElement(context, element, "element");
			temp.setAttributeNS(SchemaConstants.NS_URI_XSD_2001, "name", current.getKey());
			temp.setAttributeNS(SchemaConstants.NS_URI_XSD_2001, "type", typeName);
			// TODO: create a new annotation to be used in a POJO setter that defines if that POJO field can be null (only valid for non primitive types)
			temp.setAttributeNS(SchemaConstants.NS_URI_XSD_2001, "nillable", (isPOJO) ? "true" : "false");
		}
		
		// register the new type
		types.put(classRef, pojoName);
		
		return pojoName;
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
		// check whether the given class type is a primitive
		if (isPrimitive(type))
			return PREFIX_XMLSCHEMA + ":" + mapToSchemaType(type);
		else
		// check whether the given class type is an enumeration
		if (isEnumeration(type))
			return generateEnumType(context, null, type);
		else
			return generateBeanType(context, null, type);
	}
	
	protected String generateEnumType( SchemaContext context, String name, Class<?> classRef )
	{
		if (classRef == null)
			throw new IllegalArgumentException("The schema element can not be null");
		if (context == null)
			throw new IllegalArgumentException("The schema context can not be null");
		
		// check whether the given type is already generated
		String typeName = types.get(classRef);
		if (typeName != null) return typeName;
		// build the full qualified type name
		typeName = name;
		if (typeName == null) typeName = classRef.getSimpleName() + SUFFIX_TYPE;
		typeName = PREFIX_TYPES + ":" + typeName;
		
		Element element = createElement(context, context.schemaElement, "simpleType");
		element.setAttribute("name", typeName);
		element = createElement(context, element, "restriction");
		element.setAttribute("base", PREFIX_XMLSCHEMA + ":" + mapToSchemaType(String.class));

		// insert all valid names of the enumeration
		Object values[] = classRef.getEnumConstants();
		for (Object current : values)
		{
			Element temp = createElement(context, element, "enumeration");
			temp.setAttributeNS(SchemaConstants.NS_URI_XSD_2001, "value", current.toString());
		}
				
		// register the new type
		types.put(classRef, typeName);
		
		return typeName;
	}
	
	// TODO: use the 'BeanObject' logic to extract POJO fields
	protected Map<String,Class<?>> extractBeanFields( Class<?> classRef )
	{
		Map<String, Class<?>> output = new HashMap<String, Class<?>>();
				
		Method[] methods = classRef.getMethods();
		
		// TODO: using 'getDeclaredFields', inherited fields will not found
		for (Field field : classRef.getDeclaredFields())
		{
			String fieldName = null;
			
			corinna.rpc.annotation.Field annot = field.getAnnotation(corinna.rpc.annotation.Field.class);
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
