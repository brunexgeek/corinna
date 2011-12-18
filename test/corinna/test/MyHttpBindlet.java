package corinna.test;

import java.io.IOException;

import javax.bindlet.BindletModel;
import javax.bindlet.BindletOutputStream;
import javax.bindlet.BindletModel.Model;
import javax.bindlet.http.HttpBindlet;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;

import corinna.exception.BindletException;

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
	public void init() throws BindletException
	{

	}

}
