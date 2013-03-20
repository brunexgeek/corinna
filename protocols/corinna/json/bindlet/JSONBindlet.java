package corinna.json.bindlet;


import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;

import javax.bindlet.Bindlet;
import javax.bindlet.BindletModel;
import javax.bindlet.IBindletAuthenticator;
import javax.bindlet.BindletModel.Model;
import javax.bindlet.IComponentInformation;
import javax.bindlet.exception.BindletException;
import javax.bindlet.http.HttpMethod;
import javax.bindlet.http.HttpStatus;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;
import javax.bindlet.http.io.HttpBindletInputStream;
import javax.bindlet.io.BindletOutputStream;
import javax.bindlet.rpc.IProcedureCall;

import corinna.core.ContextInfo;
import corinna.http.core.HttpUtils;
import corinna.json.core.JSONObject;
import corinna.json.core.JSONProcedureCall;
import corinna.json.exception.JSONRPCErrorCode;
import corinna.json.exception.JSONRPCException;
import corinna.rpc.BeanObject;
import corinna.rpc.ProcedureCall;
import corinna.rpc.ReflectionUtil;


/**
 * Implementation of a bindlet for JSON-RPC protocol.
 * 
 * @author Bruno Ribeiro
 */
@SuppressWarnings("serial")
public abstract class JSONBindlet extends Bindlet<IHttpBindletRequest, IHttpBindletResponse>
{

	private static final String COMPONENT_NAME = "JSON Bindlet";

	private static final String COMPONENT_VERSION = "1.0";

	private static final String COMPONENT_IMPLEMENTOR = "Bruno Ribeiro & JSON.org";

	private static final String JSON_CONTENT_TYPE = "application/json";

	private static final String INIT_PARAM_IS_RESTRICTED = "isRestricted";
	
	private static IComponentInformation COMPONENT_INFO = new ContextInfo(COMPONENT_NAME,
		COMPONENT_VERSION, COMPONENT_IMPLEMENTOR);

	private IBindletAuthenticator authenticator = null;
	
	public JSONBindlet() throws BindletException
	{
		super();
	}

	protected abstract Object doCall( IProcedureCall request ) throws BindletException;

	@Override
	public void process( IHttpBindletRequest request, IHttpBindletResponse response )
		throws BindletException, IOException
	{
		// check whether we have a valid request
		if (request.getHttpMethod() != HttpMethod.POST && request.getHttpMethod() != HttpMethod.GET)
		{
			try
			{
				response.sendError(HttpStatus.METHOD_NOT_ALLOWED);
			} catch (Exception e)
			{
				throw new BindletException("Failed to send a HTTP error", e);
			}
			return;
		}
		
		if (isRestricted() && !doAuthentication(request, response)) return;
		
		Exception exception = null;
		Object result = null;
		Charset charset = Charset.defaultCharset();
		JSONProcedureCall call = null;
		
		try
		{
			charset = getCharset(request);
			call = getProcedureCall(request);
			call.setParameter(ProcedureCall.PARAM_REQUEST, request);
			call.setParameter(ProcedureCall.PARAM_RESPONSE, response);
			result = doCall(call);
		} catch (Exception e)
		{
			exception = e;
		}

		try
		{
			setResponse(response, charset, result, exception, (call == null) ? null : call.getId());
		} catch (IOException e)
		{
			throw new BindletException("Error writing the JSON response", e);
		}
	}

	@Override
	public IComponentInformation getBindletInfo()
	{
		return COMPONENT_INFO;
	}

	public void setResponse( IHttpBindletResponse response, Charset charset, Object returnValue,
		Exception exception, Object id ) throws IOException
	{
		if (response.isClosed()) return;

		if (charset == null) charset = Charset.defaultCharset();

		response.setCharacterEncoding(charset);
		response.setContentType(JSON_CONTENT_TYPE);

		BindletOutputStream out = response.getOutputStream();
		try
		{
			if (!out.isClosed() && out.writtenBytes() == 0)
			{
				JSONObject json = new JSONObject();
				json.put("jsonrpc", "2.0");
				json.put("id", id);
				
				if (exception != null)
				{
					JSONObject error = new JSONObject();
					error.put("code", -32000);
					error.put("message", exception.getMessage());
					json.put("error", error);					
				}
				else
				{
					//if (returnValue == null) returnValue = "";
					json.put("result", returnValue);
				}
				
				// TODO: handle the NullPointerException of "json.toString()" 
				byte[] output = json.toString().getBytes(charset);
				out.write(output);
			}
		} catch (Exception e)
		{
			// suprime os erros
		}
		if (out != null && !out.isClosed()) out.close();
	}

	protected Charset getCharset( IHttpBindletRequest request )
	{
		try
		{
			// check whether we have a valid request charset
			String value = request.getCharacterEncoding();
			if ( Charset.isSupported(value) ) return Charset.forName(value);
			// check whether we have a valid HTTP field "Accept-Charset"
			value = request.getHeader("Accept-Charset");
			return HttpUtils.getAcceptableCharset(value);
		} catch (Exception e)
		{
			return Charset.defaultCharset();
		}

	}
	
	protected JSONProcedureCall getProcedureCall( IHttpBindletRequest request ) throws JSONRPCException
	{
		JSONProcedureCall procedureCall;
		try
		{
			Charset charset = getCharset(request);
			HttpBindletInputStream is = (HttpBindletInputStream) request.getInputStream();

			// try to extract the procedure from request body
			String content = is.readText(charset);
			if (content == null || content.isEmpty())
				throw new JSONRPCException(JSONRPCErrorCode.INVALID_REQUEST);
			JSONObject json = new JSONObject(content);
			// get the method name
			String method = json.optString("method");
			if (method == null || method.isEmpty())
				throw new JSONRPCException(JSONRPCErrorCode.INVALID_REQUEST);
			// create the procedure call
			procedureCall = new JSONProcedureCall(method);
			procedureCall.setId(json.opt("id"));
			JSONObject params = json.optJSONObject("params");
			if (params != null)
			{
				Iterator<String> keys = params.keys();
				// TODO: fill java beans when not primitive types are used
		        while (keys.hasNext())
		        {
		        	String key = keys.next().toString();
		        	Object value = params.opt(key);
		        	
		        	if (value instanceof JSONObject)
		        		value = JSONObject.toJavaBean((JSONObject) value);
		        	else
		        	if (!BeanObject.isPrimitive(value.getClass()))
		        		throw new JSONRPCException(JSONRPCErrorCode.INVALID_PARAMS);
		        	
					procedureCall.setParameter(key, value);
		        }
			}
			else
			if (!json.isNull("params"))
				throw new JSONRPCException(JSONRPCErrorCode.INVALID_PARAMS);
		} catch (JSONRPCException e)
		{
			throw e;
		} catch (Exception e)
		{
			throw new JSONRPCException(JSONRPCErrorCode.PARSE_ERROR);
		}
		return procedureCall;
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
	
}
