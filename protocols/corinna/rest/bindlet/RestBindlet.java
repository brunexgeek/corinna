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
import javax.bindlet.BindletModel;
import javax.bindlet.BindletModel.Model;
import javax.bindlet.IBindletAuthenticator;
import javax.bindlet.IComponentInformation;
import javax.bindlet.exception.BindletException;
import javax.bindlet.http.HttpMethod;
import javax.bindlet.http.HttpStatus;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;
import javax.bindlet.rpc.IProcedureCall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import corinna.core.ContextInfo;
import corinna.rest.core.RestProtocolHandler;
import corinna.rpc.ProcedureCall;
import corinna.rpc.ReflectionUtil;

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

	private Logger log = LoggerFactory.getLogger(RestBindlet.class);
			
	private static final String COMPONENT_NAME = "REST Bindlet";

	private static final String COMPONENT_VERSION = "1.0";

	private static final String COMPONENT_IMPLEMENTOR = "Bruno Ribeiro";

	private static final String INIT_PARAM_IS_RESTRICTED = "isRestricted";

	private static IComponentInformation COMPONENT_INFO = new ContextInfo(COMPONENT_NAME,
		COMPONENT_VERSION, COMPONENT_IMPLEMENTOR);

	private IBindletAuthenticator authenticator = null;

	public RestBindlet() throws BindletException
	{
	}

	protected abstract Object doCall( IProcedureCall request ) throws BindletException;

	@Override
	public void process( IHttpBindletRequest request, IHttpBindletResponse response )
		throws BindletException, IOException
	{
		Exception exception = null;
		Object result = null;
		RestProtocolHandler handler = getProtocolHandler();
		Charset charset = handler.getProcedureCharset(request);

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
		ProcedureCall call = null;
		try
		{
			call = handler.readRequest(request);
			call.setParameter(ProcedureCall.PARAM_REQUEST, request);
			call.setParameter(ProcedureCall.PARAM_RESPONSE, response);
			result = doCall(call);
		} catch (Exception e)
		{
			exception = e;
			log.error("Error processing REST bindlet", e);
		}

		try
		{
			if (response.isClosed()) return;

			// TODO: this shouldn't be here!
			response.addHeader("Access-Control-Allow-Origin", "*");
			response.addHeader("Access-Control-Allow-Methods", "*");
			response.setCharacterEncoding(charset);

			if (exception == null)
				handler.writeResponse(response, call, result);
			else
				handler.writeException(response, call, exception);
		} catch (IOException e)
		{
			throw new BindletException("Error writing the REST response", e);
		}
	}

	@Override
	public IComponentInformation getBindletInfo()
	{
		return COMPONENT_INFO;
	}


	public boolean isRestricted()
	{
		String value = getInitParameter(INIT_PARAM_IS_RESTRICTED);
		return (authenticator != null && (value != null && value.equalsIgnoreCase("true")));
	}
	
	protected boolean doAuthentication( IHttpBindletRequest request, IHttpBindletResponse response )
	throws BindletException, IOException
	{
		if (authenticator != null)
			return authenticator.authenticate(request, response);
		else
			throw new BindletException("No authenticator configured");
	}
	
	protected void setAuthenticator( IBindletAuthenticator authenticator )
	{
		this.authenticator = authenticator;
	}
	
	protected IBindletAuthenticator getAuthenticator()
	{
		return authenticator;
	}

	@Override
	public Model getBindletModel()
	{
		try
		{
			BindletModel model = (BindletModel) ReflectionUtil.getAnnotation(this.getClass(), BindletModel.class);
			if (model == null) return Model.STATEFULL;
			return model.value();
		} catch (Exception e)
		{
			return Model.STATEFULL;
		}
	}
	
	protected RestProtocolHandler getProtocolHandler() throws BindletException
	{
		try
		{
			return new RestProtocolHandler( getBindletConfig() );
		} catch (Exception e)
		{
			throw new BindletException("Error creating a REST protocol handler", e);
		}
	}
	
}
