///*
// * Copyright 2011 Bruno Ribeiro <brunei@users.sourceforge.net>
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
//package corinna.util.conf;
//
//
//
//public class ConfigurationWriter
//{
//
//	
//	protected IConfiguration config;
//	
//	
//	public ConfigurationWriter( IConfiguration config ) throws NullPointerException
//	{
//		if (config == null) throw new NullPointerException("Invalid configuration");
//		
//		this.config = config;
//	}
//	
//	
//	public void setString( String key, String value )
//	{
//		config.setValue(key, value);
//	}
//	
//	
//	public void setInteger( String key, Integer value )
//	{
//		setValue(key, value);
//	}
//	
//	
//	public void setLong( String key, Long value )
//	{
//		setValue(key, value);
//	}
//	
//	
//	public void setDouble( String key, Double value )
//	{
//		setValue(key, value);
//	}
//	
//	public void setFloat( String key, Float value )
//	{
//		setValue(key, value);
//	}
//	
//	public void setBoolean( String key, Boolean value )
//	{
//		setValue(key, value);
//	}
//	
//	
//	/**
//	 * Define um valor de configuração com um dado de tipo arbitrário.
//	 * 
//	 * @param section Nome da seção.
//	 * @param key Nome da chave.
//	 * @param value Valor de tipo arbitrário. Se for passado <code>null</code> nenhum valor
//	 *     será definido.
//	 */
//	public void setValue( String key, Object value )
//	{
//		if (value == null) return;
//		config.setValue( key,value.toString() );
//	}
//}
