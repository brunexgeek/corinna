package corinna.test;

import java.util.UUID;

import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.wsdl.extensions.schema.Schema;

import org.w3c.dom.Element;

import com.ibm.wsdl.extensions.schema.SchemaConstants;
import com.ibm.wsdl.extensions.schema.SchemaImpl;

import corinna.rpc.BeanObject;
import corinna.rpc.ClassDescriptor;
import corinna.rpc.IBeanObject;
import corinna.soap.core.SchemaGenerator;


public class MainTest
{

	public static void main( String[] args ) throws Exception
	{
		/*WSDLFactory factory = WSDLFactory.newInstance();
		
		ClassDescriptor desc = new ClassDescriptor(ServiceInterface.class);
		
		WsdlGenerator wgen = new WsdlGenerator();
		Definition def = wgen.generateWsdl(desc, "http://vaas.cpqd.com.br/vaas/soap/", "http://vaas.cpqd.com.br/tts.wsdl",
			"http://vaas.cpqd.com.br/tts.xsd");
		
		WSDLWriter wr = factory.newWSDLWriter();
		wr.writeWSDL(def, System.out);*/
		MyPOJO A = new MyPOJO( new MyPOJO() );
		MyPOJO B = new MyPOJO( );
		A.setActive(true);
		A.setState(MyEnum.PARADA);
		IBeanObject bean = new BeanObject(A);
		bean.populate(B);
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
	
	public static enum MyEnum
	{
		
		PARADA,
		
		ANDANDO
		
	}
	
	public static class MyPOJO
	{
		private MyEnum state = MyEnum.ANDANDO;

		private boolean active = false;
	
		private MyPOJO pojo = null;
		
		private String name = UUID.randomUUID().toString();
		
		public MyPOJO( MyPOJO pojo )
		{
			this.setPojo(pojo);
		}
		
		public MyPOJO( )
		{
		}
		
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

		public void setPojo( MyPOJO pojo )
		{
			this.pojo = pojo;
		}

		public MyPOJO getPojo()
		{
			return pojo;
		}

		public void setName( String name )
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}
		
	}
	
}
