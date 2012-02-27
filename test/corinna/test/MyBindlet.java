package corinna.test;

import java.io.IOException;

import javax.bindlet.BindletOutputStream;
import javax.bindlet.soap.ISoapBindletRequest;
import javax.bindlet.soap.ISoapBindletResponse;
import javax.bindlet.soap.SoapBindlet;

import corinna.exception.BindletException;


public class MyBindlet extends SoapBindlet
{

	private static final long serialVersionUID = -5614532714152320386L;
	
	public MyBindlet( ) throws BindletException
	{
		super();
	}
	
	@Override
	public void doPost( ISoapBindletRequest request, ISoapBindletResponse response ) throws BindletException, IOException
	{
		response.setContentType("text/html", "UTF-8");
		BindletOutputStream stream = response.getOutputStream();
		stream.write("WSDL");
		stream.close();
	}
	
	@Override
	public void doWsdl( ISoapBindletRequest request, ISoapBindletResponse response ) throws BindletException, IOException
	{
		response.setContentType("text/html", "UTF-8");
		BindletOutputStream stream = response.getOutputStream();
		stream.write("WSDL");
		stream.close();
	}

	@Override
	public boolean isRestricted()
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
