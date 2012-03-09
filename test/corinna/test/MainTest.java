package corinna.test;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;

import org.w3c.dom.Element;

import com.ibm.wsdl.extensions.schema.SchemaConstants;
import com.ibm.wsdl.extensions.schema.SchemaImpl;

import corinna.service.rpc.ClassDescriptor;
import corinna.service.rpc.Parameter;
import corinna.service.rpc.PublicProcedure;
import corinna.service.rpc.annotation.Field;
import corinna.soap.core.SchemaGenerator;
import corinna.soap.core.WsdlGenerator;


public class MainTest
{

	public static void main( String[] args ) throws Exception
	{
		WSDLFactory factory = WSDLFactory.newInstance();
		/*Definition def = factory.newDefinition();
		Service serv = def.createService();
		Port p = def.createPort();
		p.setName("teste");
		def.setTargetNamespace("http://vaas.cpqd.com.br/tts.wsdl");
		def.addNamespace("xsds", "http://vaas.cpqd.com.br/tts.xsd");
		
		def.addService(serv);
		
		generateTypes(def, ServiceInterface.class, "http://vaas.cpqd.com.br/tts.xsd");*/
		
		ClassDescriptor desc = new ClassDescriptor(ServiceInterface.class);
		
		WsdlGenerator wgen = new WsdlGenerator();
		Definition def = wgen.generateWsdl(desc, "http://vaas.cpqd.com.br/tts.wsdl",
			"http://vaas.cpqd.com.br/tts.xsd");
		
		WSDLWriter wr = factory.newWSDLWriter();
		wr.writeWSDL(def, System.out);

		
	}
	
	public static void generateTypes( Definition def, Class<?> classRef, String targetNamespace ) throws Exception
	{
		Types types = def.getTypes();
		if (types == null)
		{
			types = def.createTypes();
			def.setTypes(types);
		}

		ClassDescriptor desc = new ClassDescriptor(classRef);
		
		SchemaGenerator ps = new SchemaGenerator();
		Element root = ps.generateSchema(desc, "http://vaas.cpqd.com.br/tts.xsd");
		
        Schema schema = new SchemaImpl();
        schema.setElement(root);
        schema.setElementType( SchemaConstants.Q_ELEM_XSD_2001 );
        
		types.addExtensibilityElement(schema);
		
	}
	
	public static interface ServiceInterface
	{
		
		@PublicProcedure
		public void calculate( 
			@Parameter(name="a") int a, 
			@Parameter(name="b") MyPOJO b );
		
	}
	
	public static enum MyEnum
	{
		
		PARADA,
		
		ANDANDO
		
	}
	
	public static class MyPOJO
	{
		private MyEnum state;

		private boolean active;
	
		public void setState( MyEnum state )
		{
			this.state = state;
		}

		public MyEnum getState()
		{
			return state;
		}

		public void setActive( boolean active )
		{
			this.active = active;
		}

		public boolean isActive()
		{
			return active;
		}
		
	}
	
}
