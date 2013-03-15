/*
 * Copyright 2011-2013 Bruno Ribeiro
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
package corinna.rpc;

import java.lang.annotation.Annotation;



public class ReflectionUtil
{

	public static <T extends Annotation> Annotation getAnnotation( Class<?> classRef, Class<T> annotationClass )
	{
		if (classRef == null || annotationClass == null) return null;
		
		// check wheter the current class is not Object
		if (classRef == Object.class) return null;
		// search for the annotation in the class
		Annotation annot = (Annotation) classRef.getAnnotation(annotationClass);
		if (annot != null) return annot;
		// search for the annotation in the superclass
		annot = getAnnotation(classRef.getSuperclass(), annotationClass);
		if (annot != null) return annot;
		//search for the annotation in each interface
		for (Class<?> intf : classRef.getInterfaces())
		{
			annot = getAnnotation(intf, annotationClass);
			if (annot != null) return annot;
		}
		
		return null;
	}
	
}
