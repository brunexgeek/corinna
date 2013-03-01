/*
 * Copyright 2011-2013 Bruno Ribeiro
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

package corinna.http.bindlet;


import java.util.LinkedList;
import java.util.List;

import javax.bindlet.http.Cookie;
import javax.bindlet.http.IHttpBindletResponse;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;


public class HttpBindletResponse extends WebBindletResponse implements IHttpBindletResponse
{

	private List<Cookie> cookies = null;
	
	public HttpBindletResponse( Channel channel, HttpVersion version )
	{
		super(channel, new DefaultHttpResponse(version, HttpResponseStatus.OK));
		cookies = new LinkedList<Cookie>();
	}

	public HttpBindletResponse( Channel channel, HttpResponse response )
	{
		super(channel, response);
		cookies = new LinkedList<Cookie>();
	}
	
	@Override
	public void addCookie( Cookie cookie )
	{
	}

	@Override
	public String encodeURL( String url )
	{
		return url;
	}

	@Override
	public String encodeRedirectURL( String url )
	{
		return url;
	}

	@Override
	protected void update()
	{
		super.update();
	}
	
}
