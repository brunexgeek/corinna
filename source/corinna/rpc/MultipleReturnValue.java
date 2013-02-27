///*
// * Copyright 2011-2012 Bruno Ribeiro
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package corinna.rpc;
//
//import java.util.Map;
//
//// TODO: arrumar essa hierarquia de classes
//// TODO: reincoporar essa classe (com novo nome?) no mecanismo de RPC
//public class MultipleReturnValue extends ParameterList
//{
//
//	private static final long serialVersionUID = 2724650042430242802L;
//
//	public static final String PAIR_SEPARATOR = "|";
//
//	public static final String KEYVALUE_SEPARATOR = ":";
//	
//	public MultipleReturnValue()
//	{
//	}
//
//	public MultipleReturnValue( String data )
//	{
//		deserialize(data);
//	}
//	
//	@Override
//	public String serialize()
//	{
//		StringBuffer sb = new StringBuffer();
//		int count = parameters.size();
//		
//		for ( Map.Entry<String, Object> entry : parameters.entrySet())
//		{
//			sb.append(entry.getKey());
//			sb.append(KEYVALUE_SEPARATOR);
//			sb.append( entry.getValue() );
//			if (--count > 0) sb.append(PAIR_SEPARATOR);
//		}
//		return sb.toString();
//	}
//
//	@Override
//	public void deserialize( String data )
//	{
//		parameters.clear();
//		parseString(this, data, PAIR_SEPARATOR, KEYVALUE_SEPARATOR);
//	}
//	
//}
