package corinna.json.bindlet;


import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;

import javax.bindlet.Bindlet;
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
import corinna.json.core.JSONObject;
import corinna.rpc.ProcedureCall;


@SuppressWarnings("serial")
public abstract class JSONBindlet extends Bindlet<IHttpBindletRequest, IHttpBindletResponse>
{

	private static final String COMPONENT_NAME = "JSON Bindlet";

	private static final String COMPONENT_VERSION = "1.0";

	private static final String COMPONENT_IMPLEMENTOR = "Bruno Ribeiro & JSON.org";

	private static final String JSON_CONTENT_TYPE = "application/json";

	private static IComponentInformation COMPONENT_INFO = new ContextInfo(COMPONENT_NAME,
		COMPONENT_VERSION, COMPONENT_IMPLEMENTOR);

	public JSONBindlet() throws BindletException
	{
		super();
	}

	protected abstract Object doCall( IProcedureCall request ) throws BindletException;

	@Override
	public void process( IHttpBindletRequest request, IHttpBindletResponse response )
		throws BindletException
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
		
		Exception exception = null;
		Object result = null;
		Charset charset = Charset.defaultCharset();
		
		try
		{
			charset = getCharset(request);
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
			throw new BindletException("Error writing the JSON response", e);
		}
	}

	@Override
	public IComponentInformation getBindletInfo()
	{
		return COMPONENT_INFO;
	}

	public void setResponse( IHttpBindletResponse response, Charset charset, Object returnValue,
		Exception exception ) throws IOException
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

				if (exception != null)
				{
					json.put("result", "ERROR");
					json.put("message", exception.getMessage());
				}
				else
				{
					json.put("result", "OK");
					if (returnValue == null) returnValue = "";
					json.put("return", returnValue);
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
			if (request.getHttpMethod() == HttpMethod.GET)
				return Charset.forName("UTF-8");
			else
				return Charset.forName(request.getCharacterEncoding());
		} catch (Exception e)
		{
			return Charset.defaultCharset();
		}

	}
	
	protected String getPrototype( IHttpBindletRequest request )
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
	
	protected IProcedureCall getProcedureCall( IHttpBindletRequest request ) throws BindletException
	{
		ProcedureCall procedureCall;
		try
		{
			Charset charset = getCharset(request);
			HttpBindletInputStream is = (HttpBindletInputStream) request.getInputStream();

			// try to extract the procedure from request body
			String proto = getPrototype(request);
			String content = is.readText(charset);
			if (content == null || content.isEmpty()) content = "{}";
			JSONObject json = new JSONObject(content);
			// create the procedure call
			procedureCall = new ProcedureCall(proto);
			@SuppressWarnings("rawtypes")
			Iterator keys = json.keys();
	        while (keys.hasNext())
	        {
	        	String key = keys.next().toString();
	        	String value = json.getString(key);
				procedureCall.setParameter(key, value);
	        }
		} catch (Exception e)
		{
			throw new BindletException("Error parsing JSON procedure call", e);
		}
		return procedureCall;
	}
	
}
