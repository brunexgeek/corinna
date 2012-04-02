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

package corinna.rest.bindlet;

import javax.bindlet.Bindlet;
import javax.bindlet.IComponentInformation;
import javax.bindlet.exception.BindletException;

import corinna.core.ContextInfo;
import corinna.rpc.IProcedureCall;


@SuppressWarnings("serial")
public abstract class RestBindlet extends Bindlet<IRestBindletRequest, IRestBindletResponse>
{
	
	private static final String COMPONENT_NAME = "REST Bindlet";

	private static final String COMPONENT_VERSION = "1.0";

	private static final String COMPONENT_IMPLEMENTOR = "Bruno Ribeiro";
	
	private static IComponentInformation COMPONENT_INFO = new ContextInfo(COMPONENT_NAME, 
		COMPONENT_VERSION, COMPONENT_IMPLEMENTOR);
	
	public RestBindlet( ) throws BindletException
	{
		super();
	}
	
	protected abstract Object doCall( IProcedureCall request ) throws BindletException;
	
	@Override
	public void process( IRestBindletRequest request, IRestBindletResponse response ) 
		throws BindletException
	{
		try
		{
			response.setReturnValue( doCall( request.getProcedureCall() ) );
		} catch (Exception e)
		{
			response.setReturnValue(null);
			response.setException(e);
		}		
	}
	
	@Override
	public IComponentInformation getBindletInfo()
	{
		return COMPONENT_INFO;
	}
	
}
