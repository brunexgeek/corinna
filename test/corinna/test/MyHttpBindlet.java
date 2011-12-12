package corinna.test;

import java.io.IOException;

import javax.bindlet.BindletOutputStream;
import javax.bindlet.http.HttpBindlet;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;

import corinna.exception.BindletException;


public class MyHttpBindlet extends HttpBindlet
{

	private static final long serialVersionUID = -5614532714152320386L;
	
	public MyHttpBindlet( ) throws BindletException
	{
	}

	@Override
	protected void doGet( IHttpBindletRequest req, IHttpBindletResponse resp )
	throws BindletException, IOException
	{
		resp.setContentLength(10);
		BindletOutputStream out = resp.getOutputStream();
		out.write("<h1>Teste de envio de texto</h1>");
		out.write("Context Path: " + req.getContextPath());
		out.write("<br/>Bindlet Path: " + req.getBindletPath());
		out.write("<br/>Resource Path: " + req.getResourcePath());
		out.write("<br/>Query String: " + req.getQueryString());
		out.write("<br/>Parameter count: " + req.getParameterNames().length);
		out.write("<br/>Request Length: " + req.getContentLength());
		out.close();
	}

	@Override
	protected void doPost( IHttpBindletRequest req, IHttpBindletResponse resp )
	throws BindletException, IOException
	{
		doGet(req,resp);
	}

	@Override
	public void init() throws BindletException
	{

	}

}
