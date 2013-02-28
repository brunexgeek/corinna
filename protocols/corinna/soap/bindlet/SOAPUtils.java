package corinna.soap.bindlet;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Text;

import corinna.rpc.BeanObject;
import corinna.rpc.TypeConverter;


// TODO: move this class to 'corinna.soap.core' package
public class SOAPUtils
{

	private static MessageFactory factory = null;

	public static MessageFactory getMessageFactory() throws SOAPException
	{
		if (factory == null)
			factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
		return factory;
	}
	
	public static SOAPMessage createMessage() throws SOAPException
	{
		return getMessageFactory().createMessage();
	}
	
	/**
	 * Generate a SOAP element containing the specified value. If the value parameter is a primitive type, the
	 * content of the generated element will be <code>value.toString</code>. If the value is a object ({@link BeanObject} or
	 * POJO), the content of the generated element will be the content of their fields.
	 *  
	 * @param parent The SOAP element that will be the parent of the new element.
	 * @param name The tag name used when the value is a primitive. 
	 * @param value The value that will be inserted on the new element.
	 * @return
	 * @throws SOAPException
	 */
	public static void generateElement( SOAPElement parent, String name, Object value ) throws SOAPException
	{
		if (value == null || TypeConverter.isPrimitive(value))
			generatePrimitiveElement(parent, name, value);
		else
			generatePOJOElement(parent, name, value);
	}
	
	public static void generatePrimitiveElement( SOAPElement parent, String name, Object value ) throws SOAPException
	{
		QName qname;
		SOAPElement element = null;
		
		// TODO: URL encode XML special symbols
		if (value == null || TypeConverter.isPrimitive(value))
		{
			// create the return value element
			qname = new QName(name);
			element = parent.addChildElement(qname);
			element.setValue( (value == null) ? "" : value.toString() );
		}
	}
	
	public static void generatePOJOElement( SOAPElement parent, String name, Object value ) throws SOAPException
	{
		BeanObject pojo;
		
		if (value == null || TypeConverter.isPrimitive(value)) 
			throw new SOAPException("Invalid POJO object");
		
		if (!(value instanceof BeanObject))
			pojo = new BeanObject(value);
		else
			pojo = (BeanObject) value;
		
		SOAPElement subElement = parent.addChildElement(name);
		
		// create the POJO fields
		Iterator<String> it = pojo.keys();
		while (it.hasNext())
		{
			String currentKey = it.next();
			Object currentValue = pojo.get(currentKey);
			
			if (currentValue != null && !TypeConverter.isPrimitive(currentValue))
			{
				//SOAPElement subElement = parent.addChildElement(currentKey);
				generatePOJOElement(subElement, currentKey, pojo.get(currentKey));
			}
			else
				generatePrimitiveElement(subElement, currentKey, pojo.get(currentKey));
		}
	}
	
	/**
	 * Returns the root {@link SOAPElement} inside of the given SOAP body.
	 * 
	 * @param body
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static SOAPElement getBodyElement( SOAPBody body )
	{
		Object current;

		if (body == null) return null;
		
		Iterator it = body.getChildElements();
		while (it.hasNext())
			if ((current = it.next()) instanceof SOAPElement) return (SOAPElement)current;

		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public static SOAPElement getNextSOAPElement( Iterator it )
	{
		if (it == null) return null;
		
		while (it.hasNext())
		{
			Object entry = it.next();
			if (entry instanceof SOAPElement) return (SOAPElement) entry;
		}
		
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public static Text getNextTextElement( Iterator it )
	{
		if (it == null) return null;
		
		while (it.hasNext())
		{
			Object entry = it.next();
			if (entry instanceof Text) return (Text) entry;
		}
		
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean canParseAsPOJO( SOAPElement element )
	{
		boolean result = false;
		
        for (Iterator iter = element.getChildElements(); iter.hasNext();) 
        {
            Object child = iter.next();
            if (child instanceof Text && ((Text)child).getData().trim().length() != 0) return false;
            if (child instanceof SOAPElement) result = true;
        }

        return result;
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean canParseAsPrimitive( SOAPElement element )
	{
        for (Iterator iter = element.getChildElements(); iter.hasNext();) 
        {
            Object child = iter.next();
            if (child instanceof SOAPElement) return false;
        }

        return true;
	}
	
	@SuppressWarnings("rawtypes")
	public static BeanObject parseAsPOJO( SOAPElement element )
	{
		BeanObject object = new BeanObject();
		
		// find all procedure parameters
		Iterator it = element.getChildElements();
		while (it.hasNext())
		{
			Object current = it.next();
			if (current instanceof SOAPElement)
			{
				SOAPElement field = (SOAPElement) current;
				String fieldName = field.getElementName().getLocalName();
				Object value;
				if (canParseAsPOJO(field))
					value = parseAsPOJO(field);
				else
				//if (canParseAsPrimitive(field))
					value = parseAsPrimitive(field);
				object.set(fieldName, value);
			}
		}
		return object;
	}

	public static String parseAsPrimitive( SOAPElement element )
	{
		/*// find all procedure parameters
		Iterator it = ;
		while (it.hasNext())
		{
			Object current = it.next();
			if (current instanceof Text)
			{
				Text content = (Text)current;
				return content.getData();
			}
		}
		return null;*/
		if (element == null) return "";
		
		Text entry = getNextTextElement( element.getChildElements() );
		if (entry != null) 
			return entry.getData();
		else
			return "";
	}
	
}
