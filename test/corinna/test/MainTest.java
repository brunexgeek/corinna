package corinna.test;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import javax.bindlet.http.IHttpBindletRequest;
import javax.bindlet.http.IHttpBindletResponse;

import corinna.http.network.HttpRequestEvent;
import corinna.network.RequestEvent;
import corinna.util.Reflection;


public class MainTest
{

	public static void main( String[] args ) throws Exception
	{
		RequestEvent<IHttpBindletRequest, IHttpBindletResponse> aaa = new RequestEvent<IHttpBindletRequest, IHttpBindletResponse>(null, null);
		//MyClass aaa = new MyClass(null, null);
		System.out.println( aaa.getResponseType() );
	}
		
	public static Class<?> getGenericType( Object obj, Class<?> refClass, int index )
	{
		if (obj == null)
			throw new IllegalArgumentException("The object can not be null");
		if (refClass == null)
			throw new IllegalAccessError("The reference class can not be null");
		if (index < 0)
			throw new IllegalAccessError("The generic parameter index must be a positive integer");
		
		Class<?> currentClass = obj.getClass();
		if (!refClass.isAssignableFrom((Class<?>)currentClass)) return null;
		
		do {
			TypeVariable<?> types[] = currentClass.getTypeParameters();
			if (types != null && types.length > index)
			{
				Type[] inners = currentClass.getTypeParameters()[index].getBounds();
				Type genericType = inners[0];
				if (genericType instanceof Class) return (Class<?>)genericType;
			}
			currentClass = currentClass.getSuperclass();
		} while (currentClass != Object.class); 
		
		return null;
	}
	
	public static class MyClass extends HttpRequestEvent
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 5831619739264603193L;

		public MyClass( IHttpBindletRequest request, IHttpBindletResponse response )
		{
			super(request, response);
		}
		
	}
	
}
