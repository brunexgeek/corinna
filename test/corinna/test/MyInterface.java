package corinna.test;

import javax.bindlet.BindletModel;
import javax.bindlet.BindletModel.Model;

import corinna.core.IServiceInterface;
import corinna.service.rpc.MultipleReturnValue;
import corinna.service.rpc.Parameter;
import corinna.service.rpc.PublicProcedure;
import corinna.util.Stateless;


public interface MyInterface extends IServiceInterface
{
	
	@PublicProcedure
	public int getCount( 
		@Parameter(name="id", required=false) Float id,
		@Parameter(name="sector") Boolean sector);
	
}
