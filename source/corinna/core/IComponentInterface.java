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

package corinna.core;

import javax.bindlet.BindletModel.Model;

import corinna.exception.ComponentException;
import corinna.rpc.annotation.RemoteMethod;
import corinna.util.StateModel;


@StateModel(Model.STATEFULL)
public interface IComponentInterface
{

	@RemoteMethod(export=false)
	public void init( Object data ) throws ComponentException;
	
	@RemoteMethod(export=false)
	public void destroy() throws ComponentException;
	
	
}
