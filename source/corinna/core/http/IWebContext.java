package corinna.core.http;


import javax.bindlet.http.IWebBindletRequest;
import javax.bindlet.http.IWebBindletResponse;

import corinna.core.IContext;


public interface IWebContext<R extends IWebBindletRequest, P extends IWebBindletResponse> extends
	IContext<R, P>
{


}
