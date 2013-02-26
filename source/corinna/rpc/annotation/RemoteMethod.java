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

package corinna.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method should be exposed through the RPC mechanism.
 * 
 * @author Bruno Ribeiro
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RemoteMethod
{
	
	/**
	 * The name by which the procedure will be accessible. If this parameter is empty or null,
	 * the method name in the Java interface will be used.
	 */
	public String name() default "";
	
	/**
	 * Indicates that the procedure can be invoked only after authentication process, if any. The
	 * default value is <code>false</code>
	 */
	public boolean secure() default false;
	
	
	/**
	 * Indicates that the method can be invoked remotely. The default value is <code>true</code>
	 */
	public boolean export() default true;
	
}
