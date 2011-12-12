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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;


public class SoapUnmarshaller
{

	private MessageFactory factory;
	
	public SoapUnmarshaller( String protocol ) throws SOAPException
	{
		factory = MessageFactory.newInstance(protocol);
	}
	
	public SOAPMessage unmarshall( InputStream message ) throws SOAPException, IOException
	{
		if (message == null || message.available() <= 0) return null;
		return factory.createMessage(null, message);
	}

	public SOAPMessage unmarshall( String message, Charset charset ) throws SOAPException, IOException
	{
		ByteArrayInputStream input = new ByteArrayInputStream(message.getBytes(charset));
		return unmarshall(input);
	}
	
}
