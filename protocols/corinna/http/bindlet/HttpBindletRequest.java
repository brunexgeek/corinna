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

package corinna.http.bindlet;

import java.security.Principal;

import javax.bindlet.http.Cookie;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.ISession;

import org.jboss.netty.handler.codec.http.HttpRequest;

//TODO: promote this class to a concrete request (not a request wrapper) PS: need to create new pipeline handlers
public class HttpBindletRequest extends WebBindletRequest implements IHttpBindletRequest
{


	public HttpBindletRequest( HttpRequest request )
	{
		super(request);
	}

	@Override
	public String getAuthType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cookie[] getCookies()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteUser()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUserInRole( String role )
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Principal getUserPrincipal()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestedSessionId()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISession getSession( boolean create )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISession getSession()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRequestedSessionIdValid()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL()
	{
		// TODO Auto-generated method stub
		return false;
	}


}
