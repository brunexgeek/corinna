package corinna.core.web;


import javax.bindlet.http.IWebBindletRequest;
import javax.bindlet.http.IWebBindletResponse;

import corinna.core.IContext;


// TODO: move to "corinna.core.http"
public interface IWebContext<R extends IWebBindletRequest, P extends IWebBindletResponse> extends
	IContext<R, P>
{


}
