package corinna.test;

import java.io.IOException;

import javax.bindlet.BindletModel;
import javax.bindlet.BindletOutputStream;
import javax.bindlet.BindletModel.Model;
import javax.bindlet.http.HttpBindlet;
import javax.bindlet.http.HttpStatus;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;

import org.jboss.netty.handler.codec.http.HttpHeaders;

import corinna.core.http.auth.AuthenticateResponse;
import corinna.core.http.auth.AuthorizationRequest;
import corinna.exception.BindletException;
import corinna.exception.ParseException;

@BindletModel(Model.STATELESS)
public class MyHttpBindlet extends HttpBindlet
{

	private static final long serialVersionUID = -5614532714152320386L;
	
	private Integer value = 0;
	
	public MyHttpBindlet( ) throws BindletException
	{
	}

	@Override
	protected void doGet( IHttpBindletRequest req, IHttpBindletResponse resp )
	throws BindletException, IOException
	{
		resp.setContentLength(10);
		BindletOutputStream out = resp.getOutputStream();
		out.write("<html><body><h1>Teste de envio de texto</h1>");
		out.write("Context Path: " + req.getContextPath());
		out.write("<br/>Bindlet Path: " + req.getBindletPath());
		out.write("<br/>Resource Path: " + req.getResourcePath());
		out.write("<br/>Query String: " + req.getQueryString());
		out.write("<br/>Parameter count: " + req.getParameterNames().length);
		out.write("<br/>Request Length: " + req.getContentLength());
		out.write("<br/>Shared value: " + getNextValue());
		out.write("</body></html>");
		out.close();
	}

	private int getNextValue()
	{
		synchronized (value)
		{
			return ++value;
		}
	}

	@Override
	protected void doPost( IHttpBindletRequest req, IHttpBindletResponse resp )
	throws BindletException, IOException
	{
		doGet(req,resp);
	}

	@Override
	protected boolean doAuthentication( IHttpBindletRequest req, IHttpBindletResponse resp )
	throws BindletException, IOException
	{
		String value = req.getHeader(HttpHeaders.Names.AUTHORIZATION);
		if (value != null)
		{
			AuthorizationRequest auth;
			try
			{
				auth = new AuthorizationRequest(value);
			} catch (Exception e)
			{
				resp.sendError(HttpStatus.UNAUTHORIZED);
				return false;
			}
			if (auth.getUserName().equals("admin")) return true;
		}
	
		AuthenticateResponse auth;
		try
		{
			auth = new AuthenticateResponse("Digest nonce=\"555\", realm=\"teste@fuck\"");
		} catch (Exception e)
		{
			resp.sendError(HttpStatus.UNAUTHORIZED);
			return false;
		}
		value = auth.toString();
		resp.setHeader(HttpHeaders.Names.WWW_AUTHENTICATE, value);
		resp.setStatus(HttpStatus.UNAUTHORIZED);
		return false;
	}
	
	@Override
	public void init() throws BindletException
	{

	}

	@Override
	public boolean isRestricted()
	{
		return true;
	}

}
