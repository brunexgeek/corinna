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

package corinna.bindlet.http;


import javax.bindlet.http.Cookie;
import javax.bindlet.http.IHttpBindletResponse;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;


public class HttpBindletResponse extends WebBindletResponse implements IHttpBindletResponse
{

	public HttpBindletResponse( Channel channel, HttpVersion version )
	{
		super(channel, new DefaultHttpResponse(version, HttpResponseStatus.OK));
	}

	public HttpBindletResponse( Channel channel, HttpResponse response )
	{
		super(channel, response);
	}
	
	@Override
	public void addCookie( Cookie cookie )
	{
		// TODO: implementar
	}

	@Override
	public String encodeURL( String url )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeRedirectURL( String url )
	{
		// TODO Auto-generated method stub
		return null;
	}

}
