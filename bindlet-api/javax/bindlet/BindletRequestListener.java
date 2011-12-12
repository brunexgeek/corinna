/*
 * Copyright 2011 Bruno Ribeiro <brunei@users.sourceforge.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javax.bindlet;


import java.util.EventListener;


/**
 * A BindletRequestListener can be implemented by the developer interested in being notified of
 * requests coming in and out of scope in a bindlet.
 * 
 * @since Bindlet 1.0
 * @version Bindlet 1.0
 */
public interface BindletRequestListener<R, P> extends EventListener
{

	/**
	 * Sinalize that a request was completely processed by a bindlet.
	 *  
	 * @param bindlet
	 * @param request
	 * @param response
	 */
	public void requestCompleted( IBindlet<R, P> bindlet, R request, P response );

	/**
	 * Sinalize that a request will be processed by a bindlet.
	 * 
	 * @param bindlet
	 * @param request
	 * @param response
	 */
	public void requestInitiated( IBindlet<R, P> bindlet, R request, P response );
	
}
