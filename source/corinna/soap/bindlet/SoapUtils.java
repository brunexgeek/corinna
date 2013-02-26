package corinna.soap.bindlet;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import corinna.rpc.BeanObject;
import corinna.rpc.TypeConverter;


public class SoapUtils
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
	 * @param parent
	 * @param name
	 * @param value
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
	
}
