package corinna.test;

import org.apache.log4j.BasicConfigurator;

import corinna.core.IDomain;
import corinna.core.XMLDomainParser;
import corinna.util.ClassLoaderManager;


public class MainTest
{

	public static void main( String[] args ) throws Exception
	{
		BasicConfigurator.configure();
		
		/**
		 * HTML
		 */
		
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
		context.setParameter("urlMapping", "*");
		IBindletRegistration reg = context.addBindlet("MyBindlet", MyHttpBindlet.class);
		reg.setBindletParameter("urlMapping", "*");
		reg.setBindletParameter("interfaceClass", MyInterface.class.getName());
		reg.setBindletParameter("implementationClass", MyImpl.class.getName());

		service.addContext(context);*/
		
		/**
		 * SOAP
		 */
		
		/*
		
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
		context.setParameter("urlMapping", "*");
		IBindletRegistration reg = context.addBindlet("MyBindlet", DefaultSoapBindlet.class);
		reg.setBindletParameter("urlMapping", "/*");
		reg.setBindletParameter("interfaceClass", MyInterface.class.getName());
		reg.setBindletParameter("implementationClass", MyImpl.class.getName());

		service.addContext(context);*/
		
		/*ClassDescriptor desc = new ClassDescriptor(MyInterface.class);
		WsdlGenerator wsdl = new WsdlGenerator("http://cpqd.com.br/vaas");
		wsdl.setServiceName("Voice-as-a-Service");
		System.out.println( wsdl.generateWsdl(desc) );*/
		
		/**
		 * XML Parser
		 */
		XMLDomainParser parser = new XMLDomainParser("/media/bruno/projetos/java/corinna/devel/test/corinna/test/domain.xml");
		IDomain domain = parser.parse();
		
	}
	
	
}
