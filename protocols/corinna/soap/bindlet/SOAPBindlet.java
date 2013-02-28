package corinna.soap.bindlet;

import java.io.IOException;

import javax.bindlet.BindletModel;
import javax.bindlet.BindletModel.Model;
import javax.bindlet.exception.BindletException;
import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;
import javax.bindlet.rpc.IProcedureCall;

import corinna.rpc.ProcedureCall;
import corinna.rpc.ReflectionUtil;
import corinna.soap.core.SOAPProtocolHandler;


@SuppressWarnings("serial")
public abstract class SOAPBindlet extends javax.bindlet.soap.SoapBindlet
{
	
	private SOAPProtocolHandler protocolHandler = null;
	
	public SOAPBindlet() throws BindletException
	{
		super();
	}
	
	protected IProcedureCall getProcedureCall( IHttpBindletRequest request,
		IHttpBindletResponse response ) throws BindletException
	{
		ProcedureCall procedure = getProtocolHandler().readRequest(request);

		procedure.setParameter(PARAM_REQUEST, request);
		procedure.setParameter(PARAM_RESPONSE, response);
		
		return procedure;
	}
		
	protected void doPost( IHttpBindletRequest request, IHttpBindletResponse response )
	throws BindletException, IOException
	{
		Object result = null;
		IProcedureCall procedure = null;
		SOAPProtocolHandler proto = getProtocolHandler();
		
		try
		{
			// extract the SOAP message from request content
			procedure = getProcedureCall(request, response);

			result = doCall(procedure);
		} catch (Exception e)
		{
			proto.writeException(response, e);
		}

		proto.writeResponse(response, procedure, result);
	}
	
	public abstract String getXMLSchemaNamespace();
	
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
	
	protected SOAPProtocolHandler getProtocolHandler() throws BindletException
	{
		try
		{
			if (protocolHandler == null)
				protocolHandler = new SOAPProtocolHandler( getXMLSchemaNamespace() );
			return protocolHandler;
		} catch (Exception e)
		{
			throw new BindletException("Error creating a SOAP protocol handler", e);
		}
	}
	
}
