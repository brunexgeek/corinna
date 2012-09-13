/*
 * Copyright 2011-2012 Bruno Ribeiro
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

import javax.bindlet.http.IWebBindletResponse;


public interface IRestBindletResponse extends IWebBindletResponse
{
	
	public Object getReturnValue();
	
	public void setReturnValue( Object value );
	
	public RestStatus getStatus();
	
	public void setStatus( RestStatus status );

	public Exception getException();
	
	public void setException( Exception exception );

	public enum RestStatus
	{
		
		ERROR,
		
		OK;
		
	}
	
}
