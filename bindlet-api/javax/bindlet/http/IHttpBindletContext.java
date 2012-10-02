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

package javax.bindlet.http;


import javax.bindlet.IBindletContext;


public interface IHttpBindletContext extends IBindletContext
{

	/**
	 * Create a new authentication token based on the specified request.
	 * 
	 * @param request
	 * @return
	 */
	//public IAuthToken createAuthToken( IHttpBindletRequest request )
	//	throws NoSuchAlgorithmException;

	/**
	 * Create a new authentication token parsing the given input data. The input data must obay the
	 * RFC 2617 format (can be a "WWW-Authenticate Response Header" or
	 * "Authorization Request Header"). If the input data is invalid or the given authentication
	 * method is not supported, this method returns <code>null</code>.
	 * 
	 * @param data
	 * @return
	 */
	//public IAuthToken createAuthToken( String data );

}
