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

package corinna.rpc.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Inherited
// TODO: rename to 'RemoteParam'
public @interface Parameter
{

	/**
	 * The name by which the procedure parameter will be accessible.
	 * 
	 * @return Nome do parâmetro.
	 */
	public String name();

	/**
	 * Indicates that the procedure parameter is required. Optional parameters can not be of
	 * primitive types.
	 * 
	 * @return <code>True</code> if the parameter is required.
	 */
	public boolean required() default true;
	
	public boolean isPublic() default true;

}
