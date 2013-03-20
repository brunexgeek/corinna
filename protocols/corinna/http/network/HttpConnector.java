/*
 * Copyright 2011-2012 Bruno Ribeiro>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package corinna.http.network;

import java.security.KeyStore;
import java.security.Security;

import javax.bindlet.BindletModel.Model;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

import corinna.exception.ConnectorException;
import corinna.network.Connector;
import corinna.network.IConnectorConfig;
import corinna.network.IProtocol;
import corinna.util.ResourceLoader;
import corinna.util.StateModel;

/**
 * Implements a network connector for HTTP requests.
 * 
 * <h1>Custom parameters</h1>
 * 
 * <p>This connector support a set of parameters through which it's possible to customize the connector
 * behavior. The following list show all supported parameters for this implementation:</p>
 * 
 * <ul>
 * <li><strong>EnableSSL:</strong> allow to enable/disable the SSL feature.</li>
 * <li><strong>KeystoreFileName:</strong> define the keystore file name. This parameter must be set 
 * if the SSL is enable.</li>
 * <li><strong>KeystorePassword:</strong> define the password used to access the keystore.</li>
 * <li><strong>PrivateKeyPassword:</strong> define the password used to access the private key.</li>
 * </ul>
 * 
 * @author Bruno Ribeiro
 */
public class HttpConnector extends Connector
{

	private static final String CONFIG_CERTS_FILE = "KeystoreFileName";

	private static final String CONFIG_ENABLE_SSL = "EnableSSL";

	private static final String CONFIG_KS_PASSWD = "KeystorePassword";
	
	private static final String CONFIG_PK_PASSWD = "PrivateKeyPassword";

	private HttpStreamHandler channelHandler;
	
	private SSLContext sslContext;
	
	private boolean enableSSL = false;
	
	public HttpConnector( IConnectorConfig config ) throws ConnectorException
	{
		super(config);
		
		StateModel state = HttpStreamHandler.class.getAnnotation(StateModel.class);
		if (state != null && state.value() == Model.STATELESS)
			this.channelHandler = new HttpStreamHandler(this);
		else
			this.channelHandler = null;
	
		// check if the SSL is enable
		enableSSL = (config.getParameter(CONFIG_ENABLE_SSL, "false").equalsIgnoreCase("true"));
		initSSL();
	}
	
	protected void initSSL() throws ConnectorException
	{
		if (!enableSSL) return;
		
		IConnectorConfig config = getConfig();
		String ksPasswd = null;
		String pkPasswd = null;
		String keystoreFileName = null;
		
		try
		{
			keystoreFileName = config.getParameter(CONFIG_CERTS_FILE);
			ksPasswd = config.getParameter(CONFIG_KS_PASSWD);
			pkPasswd = config.getParameter(CONFIG_PK_PASSWD);
		} catch (Exception e)
		{
			throw new ConnectorException("Error initializing SSL mechanism", e);
		}
		
		try
		{
			// make sure that JSSE is available
			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			// Note: a keystore is where keys and certificates are kept. Both the keystore and 
			//       individual private keys should be password protected.
			KeyStore keystore = KeyStore.getInstance("JKS");
			keystore.load( ResourceLoader.getResourceAsStream(keystoreFileName), ksPasswd.toCharArray());
			// a KeyManagerFactory is used to create key managers
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			// initialize the KeyManagerFactory to work with our keystore
			kmf.init(keystore, pkPasswd.toCharArray());
			// Note: an SSLContext is an environment for implementing JSSE. It is used to create 
			//       a ServerSocketFactory.
			sslContext = SSLContext.getInstance("SSLv3");
			sslContext.init(kmf.getKeyManagers(), null, null);
		} catch (Exception e)
		{
			throw new ConnectorException("Error initializing network connector", e);
		}
	}
	
	@Override
	public ChannelPipeline getPipeline() throws Exception
	{
		ChannelPipeline pipeline = Channels.pipeline();
		
		if (enableSSL)
		{
			SSLEngine sslEngine = sslContext.createSSLEngine();
			sslEngine.setUseClientMode(false);
			pipeline.addLast("ssl", new SslHandler(sslEngine));
		}
		pipeline.addLast("decoder", new HttpRequestDecoder(1024, 4096, 8192));
		pipeline.addLast("aggregator", new HttpChunkAggregator(1024 * 1024));
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());

		HttpStreamHandler handler = channelHandler;
		if (handler == null) handler = new HttpStreamHandler(this);
		pipeline.addLast("handler", handler);

		return pipeline;
	}

	@Override
	public IProtocol getProtocol()
	{
		return HttpProtocol.getInstance();
	}

}
