package corinna.rest.core;


import java.io.IOException;
import java.nio.charset.Charset;

import javax.bindlet.IBindletConfig;
import javax.bindlet.exception.BindletException;
import javax.bindlet.http.HttpMethod;
import javax.bindlet.http.HttpStatus;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;
import javax.bindlet.io.BindletInputStream;
import javax.bindlet.io.BindletOutputStream;
import javax.bindlet.rpc.IProcedureCall;

import org.jboss.netty.handler.codec.http.HttpHeaders;

import corinna.network.IProtocol;
import corinna.network.IProtocolHandler;
import corinna.rest.network.RestProtocol;
import corinna.rpc.ParameterList;
import corinna.rpc.ProcedureCall;


public class RestProtocolHandler implements
	IProtocolHandler<IHttpBindletRequest, IHttpBindletResponse>
{

	/**
	 * Parameter name to define a forced character encoding for received texts.
	 */
	public static final String PARAMETER_FORCEENCODING = "rpc.forceEncoding";

	private static final String MIME_TYPE = "application/x-www-form-urlencoded";

	public static final String PARAMETER_COMPATILITY = "rpc.compatibilityMode";

	private IBindletConfig config = null;

	public RestProtocolHandler( IBindletConfig config )
	{
		this.config = config;
	}

	@Override
	public ProcedureCall readRequest( IHttpBindletRequest request ) throws BindletException
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
			if (proto == null && isCompatibilityMode())
			{
				proto = (String) list.getParameter("method", null);
				if (proto != null)
				{
					int dot = proto.lastIndexOf('.');
					if (dot >= 0 && dot < proto.length()) proto = proto.substring(dot + 1);
				}
			}
			// check whether we have a valid prototype
			if (proto == null) throw new BindletException("Missing procedure prototype");
			// create the procedure call
			procedureCall = new ProcedureCall(proto);
			for (String key : keys)
				procedureCall.setParameter(key, list.getParameter(key, ""));
		} catch (BindletException e)
		{
			throw e;
		} catch (Exception e)
		{
			throw new BindletException("Error parsing REST procedure call", e);
		}
		return procedureCall;
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

	@Override
	public void writeResponse( IHttpBindletResponse response, IProcedureCall procedure,
		Object returnValue ) throws BindletException, IOException
	{
		if (response.isClosed()) return;

		response.setContentType("text/xml");
		String text = String.format("<%s><url>%s</url></%s>", procedure.getMethodPrototype(),
			returnValue, procedure.getMethodPrototype());
		byte[] output = text.getBytes(response.getCharacterEncoding());

		response.setContentLength(output.length);
		BindletOutputStream out = response.getOutputStream();
		try
		{
			if (!out.isClosed() && out.writtenBytes() == 0) out.write(output);
		} catch (Exception e)
		{
			// suprime os erros
		}
		if (out != null && !out.isClosed()) out.close();
	}

	@Override
	public void writeException( IHttpBindletResponse response, IProcedureCall procedure,
		Exception exception ) throws BindletException, IOException
	{
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

		if (response.isClosed()) return;

		// find the first error with a message
		Throwable t = exception;
		while (t != null && t.getCause() != null && t.getCause().getMessage() != null)
		{
			t = t.getCause();
		}

		response.setContentType("text/xml");
		String text = String.format("<error><message>%s</message></error>", t.getMessage());
		byte[] output = text.getBytes(response.getCharacterEncoding());

		response.setContentLength(output.length);
		BindletOutputStream out = response.getOutputStream();
		try
		{
			if (!out.isClosed() && out.writtenBytes() == 0) out.write(output);
		} catch (Exception e)
		{
			// suprime os erros
		}
		if (out != null && !out.isClosed()) out.close();
	}

	@Override
	public IProtocol getProtocol()
	{
		return RestProtocol.getInstance();
	}

	public Charset getProcedureCharset( IHttpBindletRequest request )
	{
		try
		{
			String encoding = config.getBindletParameter(PARAMETER_FORCEENCODING);
			if (encoding != null && !encoding.isEmpty())
				return Charset.forName(encoding);
			else
				/*
				 * if (request.getHttpMethod() == HttpMethod.GET ) return Charset.forName("UTF-8");
				 * else
				 */
				return Charset.forName(request.getCharacterEncoding());
		} catch (Exception e)
		{
			return Charset.defaultCharset();
		}
	}

	protected boolean isCompatibilityMode()
	{
		String option = config.getBindletParameter(PARAMETER_COMPATILITY);
		return (option != null && option.equalsIgnoreCase("true"));
	}

}
