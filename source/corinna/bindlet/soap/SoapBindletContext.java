package corinna.bindlet.soap;


import javax.bindlet.soap.ISoapBindletRequest;
import javax.bindlet.soap.ISoapBindletResponse;

import corinna.bindlet.BindletContext;
import corinna.core.soap.SoapContext;

public class SoapBindletContext extends BindletContext<ISoapBindletRequest, ISoapBindletResponse>
{

	public SoapBindletContext( SoapContext context )
	{
		super(context);		
	}

}
