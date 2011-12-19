package corinna.test;

import org.apache.log4j.BasicConfigurator;

import corinna.bindlet.soap.DefaultSoapBindlet;
import corinna.core.DefaultServer;
import corinna.core.Domain;
import corinna.core.IBindletRegistration;
import corinna.core.IDomain;
import corinna.core.IServer;
import corinna.core.IService;
import corinna.core.http.HttpContext;
import corinna.core.soap.SoapContext;
import corinna.network.NetworkConfig;
import corinna.network.http.HttpNetworkConnector;
import corinna.network.web.WebNetworkConnector;


public class MainTest
{

	public static void main( String[] args ) throws Exception
	{
		BasicConfigurator.configure();
		
		/**
		 * HTTP Context
		 */

		HttpContext context1 = new HttpContext("HttpContext");
		context1.setParameter("urlMapping", "http");
		IBindletRegistration reg1 = context1.addBindlet("MyBindlet", MyHttpBindlet.class);
		reg1.setBindletParameter("urlMapping", "login");
		
		/**
		 * SOAP Context
		 */
		
		SoapContext context2 = new SoapContext("SoapContext");
		context2.setParameter("urlMapping", "soap");
		IBindletRegistration reg2 = context2.addBindlet("MyBindlet", DefaultSoapBindlet.class);
		reg2.setBindletParameter("urlMapping", "call");
		reg2.setBindletParameter("interfaceClass", MyInterface.class.getName());
		reg2.setBindletParameter("implementationClass", MyImpl.class.getName());
		
		/**
		 * Another elements
		 */
		
		IService service = new MyService("myService");
		service.addContext(context1);
		service.addContext(context2);
		service.init();
		
		NetworkConfig config = new NetworkConfig("WebConnector", "localhost", 8080);
		WebNetworkConnector connector = new WebNetworkConnector(config);
		connector.init();
		
		IServer server = new DefaultServer("MyServer");
		server.addService(service);
		server.init();
		
		IDomain domain = new Domain("vaas.cpqd.com.br");
		domain.addConnector(connector);
		domain.addServer(server);

		connector.start();
		
		/**
		 * XML Parser
		 */
		//XMLDomainParser parser = new XMLDomainParser("/media/bruno/projetos/java/corinna/devel/test/corinna/test/domain.xml");
		//IDomain domain = parser.parse();
		
	}
	
	
}
