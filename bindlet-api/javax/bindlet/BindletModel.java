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

package javax.bindlet;


import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <p>
 * Indicates the bindlet class state model.
 * </p>
 * 
 * <ul>
 * <li><strong>Statefull:</strong> the bindlet class will be instantiated for every request.</li>
 * <li><strong>Stateless:</strong> the bindlet class will be instantiated once and that instance
 * will be used across every requests.</li>
 * <li><strong>Recyclable:</strong> almost the same as statefull bindlets, but the bindlet instances
 * will be reused in future calls. Parameters to configure the recyclable behavior can be defined in
 * the bindlet context.</li>
 * </ul>
 * 
 * @author Bruno Ribeiro
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface BindletModel
{

	public Model value() default Model.STATEFULL;

	public enum Model
	{

		STATELESS,

		STATEFULL,

		RECYCLABLE

	}

}
