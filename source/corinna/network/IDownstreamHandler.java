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
package corinna.network;

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;


public interface IDownstreamHandler extends ChannelDownstreamHandler
{

	public void outgoingMessage( ChannelHandlerContext context, MessageEvent event )
		throws Exception;

	public void outgoingChannelEvent( ChannelHandlerContext context, ChannelEvent event )
		throws Exception;
	
	public void outgoingException( ChannelHandlerContext context, ExceptionEvent event ) 
		throws Exception;

}
