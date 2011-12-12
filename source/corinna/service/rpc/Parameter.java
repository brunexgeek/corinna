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

package corinna.service.rpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Inherited
public @interface Parameter
{

	/**
	 * Retorna o nome do parâmetro.
	 * 
	 * @return Nome do parâmetro.
	 */
	public String name();
	
	
	/**
	 * Indica se o valor do parâmetro deve ser fornecido obrigatoriamente. Somente parâmetros cujo
	 * tipo não é um tipo primitivo podem ser opcionais.
	 * 
	 * @return <code>True</code> se o parâmetro é obrigatório ou <code>false</code> caso contrário.
	 */
	public boolean required() default true;
	
}
