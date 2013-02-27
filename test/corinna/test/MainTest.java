package corinna.test;

import java.util.UUID;

import javax.wsdl.Definition;
import javax.wsdl.Types;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;

import org.w3c.dom.Element;

import com.ibm.wsdl.extensions.schema.SchemaConstants;
import com.ibm.wsdl.extensions.schema.SchemaImpl;

import corinna.rpc.ClassDescriptor;
import corinna.rpc.annotation.Parameter;
import corinna.rpc.annotation.RemoteComponent;
import corinna.rpc.annotation.RemoteMethod;
import corinna.soap.core.SchemaGenerator;
import corinna.soap.core.WsdlGenerator;


public class MainTest
{

	public static void main( String[] args ) throws Exception
	{
		WSDLFactory factory = WSDLFactory.newInstance();
		
		ClassDescriptor desc = new ClassDescriptor(MyPOJO.class);
		
		WsdlGenerator wgen = new WsdlGenerator(desc, "http://fuck.com");
		Definition def = wgen.generateWsdl();
		
		WSDLWriter wr = factory.newWSDLWriter();
		wr.writeWSDL(def, System.out);
		/*MyPOJO A = new MyPOJO( new MyPOJO() );
		MyPOJO B = new MyPOJO( );
		A.setActive(true);
		A.setState(MyEnum.PARADA);
		IBeanObject bean = new BeanObject(A);
		bean.populate(B);*/
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
	
	@RemoteComponent(name="MyPOJOService")
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
		
		@RemoteMethod()
		public void setState( @Parameter(name="state")MyEnum state )
		{
			this.state = state;
		}

		@RemoteMethod()
		public MyEnum getState()
		{
			return state;
		}

		@RemoteMethod()
		public void setActive( @Parameter(name="active")boolean active )
		{
			this.active = active;
		}

		@RemoteMethod()
		public boolean isActive()
		{
			return active;
		}

		@RemoteMethod()
		public void setPojo( @Parameter(name="pojo")MyPOJO pojo )
		{
			this.pojo = pojo;
		}

		@RemoteMethod()
		public MyPOJO getPojo()
		{
			return pojo;
		}
		
		@RemoteMethod()
		public void setName( @Parameter(name="name")String name )
		{
			this.name = name;
		}

		@RemoteMethod()
		public String getName()
		{
			return name;
		}
		
	}
	
}
