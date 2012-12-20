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
