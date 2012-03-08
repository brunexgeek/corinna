package corinna.test;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;

import org.w3c.dom.Element;

import corinna.service.rpc.annotation.Field;
import corinna.soap.bindlet.JavaBeanSerializer;


public class MainTest
{

	public static void main( String[] args ) throws Exception
	{
		WSDLFactory factory = WSDLFactory.newInstance();
		Definition def = factory.newDefinition();
		Service serv = def.createService();
		Port p = def.createPort();
		p.setName("teste");
		
		def.addService(serv);
		
		JavaBeanSerializer ps = new JavaBeanSerializer();
		ps.serialize(def, MyPOJO.class);
		
		WSDLWriter wr = factory.newWSDLWriter();
		wr.writeWSDL(def, System.out);

		
	}
	
	public static class MyPOJO
	{
		
		@Field(name="Money")
		private float money;

		private boolean active;
	
		public void setMoney( float money )
		{
			this.money = money;
		}

		public float getMoney()
		{
			return money;
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
