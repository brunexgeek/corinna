package corinna.http.bindlet;

import javax.bindlet.BindletModel;
import javax.bindlet.BindletModel.Model;
import javax.bindlet.exception.BindletException;

import corinna.rpc.ReflectionUtil;


@SuppressWarnings("serial")
public abstract class HttpBindlet extends javax.bindlet.http.HttpBindlet
{


	public HttpBindlet() throws BindletException
	{
		super();
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

}
