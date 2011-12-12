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

import corinna.exception.BindletException;


/**
 * This interface must be implemented by all bindlets that can be recycled for use in multiples
 * requests.
 * 
 * @author Bruno Ribeiro
 * @version Bindlet 1.0
 * @since Bindlet 1.0
 * @see BindletModel
 */
public interface IRecyclable
{

	public void recycle() throws BindletException;
	
}
