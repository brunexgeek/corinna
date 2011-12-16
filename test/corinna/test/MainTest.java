package corinna.test;

import javax.bindlet.IBindlet;

import org.apache.log4j.BasicConfigurator;

import corinna.core.DefaultServer;
import corinna.core.Domain;
import corinna.core.IBindletRegistration;
import corinna.core.IDomain;
import corinna.core.IServer;
import corinna.core.IService;
import corinna.core.soap.SoapContext;
import corinna.network.soap.SoapNetworkConnector;


public class MainTest
{

	public static void main( String[] args ) throws Exception
	{
		/*BasicConfigurator.configure();
		
		HttpNetworkConnector connector = new HttpNetworkConnector("SOAP", "http://localhost:8080", 4);
		connector.init();
		
		IServer server = new DefaultServer("MyServer");
		IService service = new MyService("myService", server);
		server.addService(service);
		
		IDomain domain = new Domain("vaas.cpqd.com.br");
		domain.addConnector(connector);
		domain.addServer(server, "SOAP");

		connector.start();

		//BindletContextConfig contextConfig = new BindletContextConfig("Test");
		HttpContext context = new HttpContext("MyContext", service);
		context.setParameter("urlMapping", "/vaas");
		IBindletRegistration reg = context.addBindlet("MyBindlet", MyHttpBindlet.class);
		reg.setBindletParameter("urlMapping", "/soap");
		reg.setBindletParameter("interfaceClass", MyInterface.class.getName());
		reg.setBindletParameter("implementationClass", MyImpl.class.getName());

		service.addContext(context);
		
		IBindlet<?,?> bindlet = context.createBindlet("MyBindlet");
		bindlet.init();*/
		
		BasicConfigurator.configure();
		
		SoapNetworkConnector connector = new SoapNetworkConnector("SOAP", "http://localhost:8080", 4);
		connector.init();
		
		IServer server = new DefaultServer("MyServer");
		IService service = new MyService("myService", server);
		server.addService(service);
		
		IDomain domain = new Domain("vaas.cpqd.com.br");
		domain.addConnector(connector);
		domain.addServer(server, "SOAP");

		connector.start();

		//BindletContextConfig contextConfig = new BindletContextConfig("Test");
		SoapContext context = new SoapContext("MyContext", service);
		context.setParameter("urlMapping", "/vaas");
		IBindletRegistration reg = context.addBindlet("MyBindlet", MyBindlet.class);
		reg.setBindletParameter("urlMapping", "/soap");
		reg.setBindletParameter("interfaceClass", MyInterface.class.getName());
		reg.setBindletParameter("implementationClass", MyImpl.class.getName());

		service.addContext(context);
		
		IBindlet<?,?> bindlet = context.createBindlet("MyBindlet");
		bindlet.init();
		
		/*ClassDescriptor desc = new ClassDescriptor(MyInterface.class);
		WsdlGenerator wsdl = new WsdlGenerator("http://cpqd.com.br/vaas");
		wsdl.setServiceName("Voice-as-a-Service");
		System.out.println( wsdl.generateWsdl(desc) );*/
	}
	
}
