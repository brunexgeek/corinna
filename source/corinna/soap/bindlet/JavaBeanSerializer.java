package corinna.soap.bindlet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.wsdl.extensions.schema.Schema;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.wsdl.extensions.schema.SchemaConstants;
import com.ibm.wsdl.extensions.schema.SchemaImpl;


public class JavaBeanSerializer
{

	Document document;
	
	public JavaBeanSerializer() throws Exception 
	{
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		document = docBuilder.newDocument();
	}

	public void serialize( Definition def, Class<?> classRef ) throws Exception
	{
		Types types = def.getTypes();
		if (types == null)
		{
			types = def.createTypes();
			def.setTypes(types);
		}

		Element root = serializeObject(classRef);
        
        Schema schema = new SchemaImpl();
        schema.setElement(root);
        schema.setElementType( SchemaConstants.Q_ELEM_XSD_2001 );
        
		types.addExtensibilityElement(schema);
		
	}
	
	protected Element serializeObject( Class<?> classRef ) throws Exception
	{
        Element root = createElement(SchemaConstants.ELEM_SCHEMA);
        processObject(classRef, root);
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
	
	protected void processObject( Class<?> classRef, Element parent )
	{
		if (classRef == null || parent == null)
			throw new IllegalArgumentException("The element or target object can not be null");

		Map<String,Class<?>> fields = extractFields(classRef);
		
		Element element = createElement(parent, "element");
		element.setAttribute("name", classRef.getName());
		element = createElement(element, "complexType");
		element = createElement(element, "sequence");
		
		for (Map.Entry<String,Class<?>> current : fields.entrySet())
		{
			Element temp = createElement(element, "element");
			temp.setAttributeNS(SchemaConstants.NS_URI_XSD_2001, "name", current.getKey());
			temp.setAttributeNS(SchemaConstants.NS_URI_XSD_2001, "type", current.getValue().getCanonicalName());
		}
	}
	
	protected Map<String,Class<?>> extractFields( Class<?> classRef )
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
			Method setter = findMethod(methods, fieldName, MethodPrefix.SET);
			if (setter == null) continue;
			
			Method getter = null;
			if (field.getType() == Boolean.class || field.getType() == boolean.class)
				getter = findMethod(methods, fieldName, MethodPrefix.IS);
			else
				getter = findMethod(methods, fieldName, MethodPrefix.GET);
			if (getter == null) continue;
			
			output.put(fieldName, field.getType());
		}
		
		return output;
	}
	
	protected Method findMethod( Method[] methods, String fieldname, MethodPrefix prefix )
	{
		if (fieldname == null || fieldname.isEmpty())
			throw new IllegalAccessError("The method name can not be null or empty");
		
		// ensure that the first character in field name is captalized
		char newName[] = fieldname.toCharArray();
		if (Character.isLowerCase( newName[0] ) )
			newName[0] = Character.toUpperCase( newName[0] );
		
		if (prefix == MethodPrefix.GET)
			fieldname = "get" + String.valueOf(newName);
		else
		if (prefix == MethodPrefix.SET)
			fieldname = "set" + String.valueOf(newName);
		else
			fieldname = "is" + String.valueOf(newName);
		
		for (Method method : methods)
			if (method.getName().equals(fieldname)) return method;
		return null;
	}
	
	public static enum MethodPrefix
	{
		
		GET,
		
		SET,
		
		IS
		
	}
	
}
