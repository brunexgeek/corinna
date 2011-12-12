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

package corinna.bindlet.rest;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.bindlet.BindletInputStream;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;

import corinna.bindlet.http.WebBindletRequest;
import corinna.service.rpc.IProcedureCall;
import corinna.service.rpc.ParameterList;
import corinna.service.rpc.ProcedureCall;

public class RestBindletRequest extends WebBindletRequest implements IRestBindletRequest
{
	
	private ProcedureCall procedureCall = null;
	
	public RestBindletRequest( HttpRequest request )
	{
		super(request);
	}
	
	private String getProcedurePrototype( )
	{
		String path = getResourcePath();
		if (path == null || path.isEmpty()) return null;
		
		int start = path.lastIndexOf("/");
		if (path.length() < start+1)
			return path.substring(start+1);
		else
			return null;
	}
	
	private String getProcedureParameters( )
	{
		if (getHttpMethod() == HttpMethod.GET.getName())
			return getQueryString();
		if (getHttpMethod() == HttpMethod.POST.getName())
		{
			try
			{
				BindletInputStream is = getInputStream();
				String value = is.readString();
				is.close();
				return value;
			} catch (IOException e)
			{
				return null;
			}
		}
		return null;
	}
	
	private Charset getProcedureCharset()
	{
		try
		{
			if (getHttpMethod() == HttpMethod.GET.getName())
				return Charset.forName("ANSI");
			else
				return Charset.forName(getCharacterEncoding());
		} catch (Exception e)
		{
			return Charset.defaultCharset();
		}
		
	}
	
	@Override
	public IProcedureCall getProcedureCall()
	{
		if (procedureCall == null)
		{
			// try to get the procedure prototype
			String proto = getProcedurePrototype();
			String params = getProcedureParameters();
			if (proto == null || params == null || proto.isEmpty()) return null;
			// create the procedure call
			try
			{
				procedureCall = new ProcedureCall(proto);
				ParameterList list = new ParameterList( getProcedureCharset() );
				ParameterList.parseString(list, params, "&", "=");
				String[] keys = list.getParameterNames();
				for (String key : keys) procedureCall.setParameter(key, list.getParameter(key, ""));
			} catch (Exception e)
			{
				procedureCall = null;
			}
		}
		return procedureCall;
	}	

}
