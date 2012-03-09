package corinna.soap.bindlet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

// TODO: move to 'corinna.soap.core'
// TODO: rename to 'SchemaGenerator'
public class JavaBeanSerializer
{

	private static final Class<?>[] TYPE_CLASSES = { Integer.class, Float.class, Long.class,
		String.class, Double.class, Boolean.class, MultipleReturnValue.class, Byte.class,
		Short.class, int.class, float.class, long.class, double.class, boolean.class, 
		byte.class, short.class };

	private static final String TYPE_NAMES[] = { "int", "float", "long", "string", 
		"double", "boolean", "string", "byte", "short", "int", "float", 
		"long", "double", "boolean", "byte", "short" };
	
	private Document document;
	
	public JavaBeanSerializer() throws Exception 
	{
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		document = docBuilder.newDocument();
	}
	
	public Element generateSchema( ClassDescriptor classDesc, String targetNamespace ) throws Exception
	{
		Element root = createElement(SchemaConstants.ELEM_SCHEMA);
        root.setAttribute("targetNamespace", targetNamespace);
        
        MethodDescriptor methods[] = classDesc.getMethods();
        
        for (MethodDescriptor method : methods)
        	generateMethod(root, method);
        
        return root;
	}
	
	protected Element createElement( Element parent, String name )
	{
		if (name == null)
			throw new IllegalArgumentException("The element name can not be null");
		
		Element element = document.createElementNS(SchemaConstants.NS_URI_XSD_2001, name);
		element.setPrefix("xsd");
		if (parent != null) parent.appendChild(element);
		return element;
	}
	
	protected Element createElement( String name )
	{
		return createElement(null, name);
	}
	
	public boolean isPrimitive( Class<?> classRef )
	{
		int k = 0;
		
		if (classRef.isPrimitive()) return true;
		
		for (k = 0; k < TYPE_NAMES.length && !TYPE_CLASSES[k].equals(classRef); ++k);
		return (k < TYPE_NAMES.length);
	}
	
	protected void generatePrimitive( Element parent, String name, Class<?> classRef )
	{
		Element element = createElement(parent, "element");
		element.setAttribute("name", name);
		element.setAttribute("type", "xsd:" + mapToSchemaType(classRef));
	}
	
	protected void generateMethod( Element schemaElement, MethodDescriptor method )
	{
		Element element = createElement(schemaElement, "element");
		element.setAttribute("name", method.getName() + "Input");
		element = createElement(element, "complexType");
		Element sequence = createElement(element, "sequence");
		
		for (int c = 0; c < method.getParameterCount(); ++c)
		{
			ParameterDescriptor param = method.getParameter(c);
			if (!param.isPublic()) continue;
			
			if (isPrimitive(param.getType()))
				generatePrimitive(sequence, param.getName(), param.getType());
			else
			{
				String typeName = generateBean(schemaElement, param.getType());
				element = createElement(sequence, "element");
				element.setAttribute("name", param.getName());
				element.setAttribute("type", "xsds:" + typeName);			
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
	
	protected String generateBean( Element schemaElement, Class<?> classRef )
	{
		if (classRef == null || schemaElement == null)
			throw new IllegalArgumentException("The element or target object can not be null");

		Map<String,Class<?>> fields = extractBeanFields(classRef);
		
		Element element = createElement(schemaElement, "element");
		element.setAttribute("name", classRef.getName() + "Type");
		element = createElement(element, "complexType");
		element = createElement(element, "sequence");
		
		for (Map.Entry<String,Class<?>> current : fields.entrySet())
		{
			Element temp = createElement(element, "element");
			temp.setAttributeNS(SchemaConstants.NS_URI_XSD_2001, "name", current.getKey());
			temp.setAttributeNS(SchemaConstants.NS_URI_XSD_2001, "type", current.getValue().getCanonicalName());
		}
		
		return classRef.getName() + "Type";
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
