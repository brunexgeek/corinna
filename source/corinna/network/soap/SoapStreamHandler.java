/*
 * Copyright 2011 Bruno Ribeiro <brunei@users.sourceforge.net>
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

package corinna.network.soap;


import java.nio.charset.Charset;

import javax.bindlet.http.HttpStatus;
import javax.bindlet.soap.ISoapBindletRequest;
import javax.bindlet.soap.ISoapBindletResponse;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;

import corinna.bindlet.soap.SoapBindletRequest;
import corinna.bindlet.soap.SoapBindletResponse;
import corinna.network.StreamHandler;
import corinna.util.StateModel;
import corinna.util.StateModel.Model;

@StateModel(Model.STATELESS)
public class SoapStreamHandler extends StreamHandler
{

	private static Logger log = Logger.getLogger(SoapStreamHandler.class);

	private SoapNetworkConnector connector;

	private SoapUnmarshaller unmarshaller;

	private SoapMarshaller marshaller;
	
	public SoapStreamHandler( SoapNetworkConnector connector ) throws SOAPException
	{
		if (connector == null)
			throw new NullPointerException("The network connector can not be null");
		
		unmarshaller = new SoapUnmarshaller(SOAPConstants.SOAP_1_1_PROTOCOL);
		marshaller = new SoapMarshaller(SOAPConstants.SOAP_1_1_PROTOCOL);
		
		this.connector = connector;
	}

	@Override
	public void incomingMessage( ChannelHandlerContext context, MessageEvent event )
		throws Exception
	{
		HttpRequest request = (HttpRequest) event.getMessage();

		ISoapBindletRequest req = new SoapBindletRequest(request, unmarshaller);
		ISoapBindletResponse res = new SoapBindletResponse( event.getChannel(), marshaller, request.getProtocolVersion() );

		SoapRequestEvent e = new SoapRequestEvent(req, res);
		try
		{
			connector.handlerRequestReceived(this, e);
		} catch (Exception ex)
		{
			res.setException(ex);
			ex.printStackTrace();
		}
		
		// check if no bindlets handle this request
		if (!e.isHandled())
			// send 'HTTP 404' to client
			res.sendError(HttpStatus.NOT_FOUND);
		else
			// flush the HTTP response content
			res.close();
		// close the connection, if necessary
		if (!HttpHeaders.isKeepAlive(request))
			event.getChannel().close();
	}
	
	protected Charset getCharset( HttpRequest request )
	{
		String value = request.getHeader( HttpHeaders.Names.CONTENT_ENCODING);
		if (value != null)
		{
			try
			{
				return Charset.forName(value);
			} catch (Exception e)
			{
				// supress any error
			}
		}
		return Charset.defaultCharset();
	}
	
}
