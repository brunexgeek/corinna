/*
 * Copyright 2011-2012 Bruno Ribeiro
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

package corinna.rest.bindlet;


import java.io.IOException;
import java.nio.charset.Charset;

import javax.bindlet.Bindlet;
import javax.bindlet.IComponentInformation;
import javax.bindlet.exception.BindletException;
import javax.bindlet.http.HttpMethod;
import javax.bindlet.http.HttpStatus;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;
import javax.bindlet.io.BindletInputStream;
import javax.bindlet.io.BindletOutputStream;
import javax.bindlet.rpc.IProcedureCall;

import org.jboss.netty.handler.codec.http.HttpHeaders;

import corinna.core.ContextInfo;
import corinna.rpc.ParameterList;
import corinna.rpc.ProcedureCall;

/**
 * Implements an abstract REST bindlet that can receive GET and POST requests.
 * 
 * <h2>Parameters</h2>
 * 
 * The following parameters can be setted for this bindlet:
 * 
 * <dl>
 *    <dt>CompatibilityMode</dt>
 *    <dd>defines whether the component should accept that the method name is 
 *    provided via "method" parameter (GET or POST) rather than be supplied in the URL.</dd>
 * </dl>
 * 
 * @author Bruno Ribeiro <brunoc@cpqd.com.br>
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings("serial")
public abstract class RestBindlet extends Bindlet<IHttpBindletRequest, IHttpBindletResponse>
{

	private static final String MIME_TYPE = "application/x-www-form-urlencoded";
	
	public static final String CONFIG_COMPATILIBITY_MODE = "CompatibilityMode"; 
	
	private static final String COMPONENT_NAME = "REST Bindlet";

	private static final String COMPONENT_VERSION = "1.0";

	private static final String COMPONENT_IMPLEMENTOR = "Bruno Ribeiro";

	private static IComponentInformation COMPONENT_INFO = new ContextInfo(COMPONENT_NAME,
		COMPONENT_VERSION, COMPONENT_IMPLEMENTOR);

	public RestBindlet() throws BindletException
	{
	}

	protected abstract Object doCall( IProcedureCall request ) throws BindletException;

	@Override
	public void process( IHttpBindletRequest request, IHttpBindletResponse response )
		throws BindletException
	{
		Exception exception = null;
		Object result = null;
		Charset charset = Charset.defaultCharset();

		if (request.getHttpMethod() != HttpMethod.GET && request.getHttpMethod() != HttpMethod.POST)
		{
			try
			{
				response.sendError(HttpStatus.METHOD_NOT_ALLOWED);
			} catch (IOException e)
			{
			}
			return;
		}

		if (isRestricted() && !doAuthentication(request, response)) return;
		
		// extract a 'ProcedureCall' from the HTTP request
		try
		{
			charset = getProcedureCharset(request);
			ProcedureCall call = (ProcedureCall) getProcedureCall(request);
			call.setParameter(PARAM_REQUEST, request);
			call.setParameter(PARAM_RESPONSE, response);
			result = doCall(call);
		} catch (Exception e)
		{
			exception = e;
		}

		try
		{
			setResponse(response, charset, result, exception);
		} catch (IOException e)
		{
			throw new BindletException("Error writing the REST response", e);
		}
	}

	public void setResponse( IHttpBindletResponse response, Charset charset, Object returnValue, Exception exception )
		throws IOException
	{
		if (response.isClosed()) return;

		if (charset == null) charset = Charset.defaultCharset();
		// TODO: this shouldn't be here!
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "*");
		response.setCharacterEncoding(charset);
		
		BindletOutputStream out = response.getOutputStream();
		try
		{
			if (!out.isClosed() && out.writtenBytes() == 0)
			{
				ParameterList buffer = new ParameterList(charset);

				if (exception != null)
				{
					buffer.setValue("result", "ERROR");
					buffer.setValue("message", exception.getMessage());
				}
				else
				{
					buffer.setValue("result", "OK");
					if (returnValue == null) returnValue = "";
					buffer.setValue("return", returnValue);
				}

				byte[] output = buffer.toString().getBytes(charset);
				out.write(output);
			}
		} catch (Exception e)
		{
			// suprime os erros
		}
		if (out != null && !out.isClosed()) out.close();
	}

	@Override
	public IComponentInformation getBindletInfo()
	{
		return COMPONENT_INFO;
	}

	protected String getProcedurePrototype( IHttpBindletRequest request )
	{
		String path = request.getResourcePath();
		if (path == null || path.isEmpty()) return null;

		int end = path.indexOf("?");
		if (end < 0) end = path.length();
		int start = path.lastIndexOf("/");
		if (start + 1 < path.length())
			return path.substring(start + 1, end);
		else
			return null;
	}

	protected String getProcedureParameters( IHttpBindletRequest request )
	{
		if (request.getHttpMethod() == HttpMethod.GET) return request.getQueryString();
		if (request.getHttpMethod() == HttpMethod.POST)
		{
			// check if the content type is valid for a POST request 
			String contentType = request.getHeader(HttpHeaders.Names.CONTENT_TYPE);
			if (!contentType.equals(MIME_TYPE)) return null;
			
			try
			{
				BindletInputStream is = request.getInputStream();
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

	protected Charset getProcedureCharset( IHttpBindletRequest request )
	{
		try
		{
			if (request.getHttpMethod() == HttpMethod.GET)
				return Charset.forName("UTF-8");
			else
				return Charset.forName(request.getCharacterEncoding());
		} catch (Exception e)
		{
			return Charset.defaultCharset();
		}

	}

	protected IProcedureCall getProcedureCall( IHttpBindletRequest request ) throws BindletException
	{
		ProcedureCall procedureCall;

		// try to get the procedure prototype
		String proto = getProcedurePrototype(request);
		String params = getProcedureParameters(request);

		try
		{
			// parse the procedure call parameters
			ParameterList list = new ParameterList(getProcedureCharset(request));
			ParameterList.parseString(list, params, "&", "=");
			String[] keys = list.getParameterNames();
			// check whether the procedure prototype was obtained
			if (proto == null)
			{
				String option = getInitParameter(CONFIG_COMPATILIBITY_MODE);
				if (option != null && option.equalsIgnoreCase("true"))
					proto = (String)list.getParameter("method", null);
			}
			// check whether we have a valid prototype
			if (proto == null)
				throw new BindletException("Missing procedure prototype");
			// create the procedure call
			procedureCall = new ProcedureCall(proto);
			for (String key : keys)
				procedureCall.setParameter(key, list.getParameter(key, ""));
		} catch (BindletException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new BindletException("Error parsing REST procedure call", e);
		}
		return procedureCall;
	}

	public abstract boolean isRestricted();
	
	protected boolean doAuthentication( IHttpBindletRequest request, IHttpBindletResponse response )
	throws BindletException
	{
		return !isRestricted();
	}
	
}
