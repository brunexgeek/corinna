package corinna.test;

import corinna.service.rpc.MultipleReturnValue;
import corinna.service.rpc.Parameter;
import corinna.service.rpc.PublicProcedure;


public interface MyInterface
{

	
	@PublicProcedure
	public int getCount( 
		@Parameter(name="id", required=false) Float id,
		@Parameter(name="sector") Boolean sector);
	
	@PublicProcedure
	public int setAttributes( 
		@Parameter(name="attributes") MultipleReturnValue atts );
	
}
