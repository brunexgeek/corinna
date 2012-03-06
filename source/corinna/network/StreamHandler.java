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

package corinna.network;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;


public abstract class StreamHandler implements IDownstreamHandler, IUpstreamHandler
{

	private static Logger log = Logger.getLogger(StreamHandler.class);
	
	@Override
	public void handleDownstream( final ChannelHandlerContext context, final ChannelEvent event )
		throws Exception
	{
		if (event instanceof MessageEvent)
		{
			try
			{
				this.outgoingMessage(context, (MessageEvent) event);
			} catch (Throwable e)
			{
				Channels.fireExceptionCaught(context, e);
				log.error("Error handling a message event", e);
			}
		}
		else
		if (event instanceof ChannelEvent)
		{
			try
			{
				this.outgoingChannelEvent(context, (ChannelEvent) event);
			} catch (Throwable e)
			{
				Channels.fireExceptionCaught(context, e);
				log.error("Error handling a channel event", e);
			}
		}
		else
		if (event instanceof ExceptionEvent)
		{
			try
			{
				outgoingException(context, (ExceptionEvent) event);
			} catch (Throwable e)
			{
				// supress any errors
			}
		}
		context.sendDownstream(event);
	}

	@Override
	public void outgoingMessage( ChannelHandlerContext context, MessageEvent event )
		throws Exception
	{
	}

	@Override
	public void outgoingChannelEvent( ChannelHandlerContext context, ChannelEvent event )
		throws Exception
	{
	}
	
	@Override
	public void outgoingException( ChannelHandlerContext context, ExceptionEvent event ) 
		throws Exception
	{
		//log.error("Outgoing exception caught", event.getCause() );
	}

	@Override
	public void handleUpstream( final ChannelHandlerContext context, final ChannelEvent event )
		throws Exception
	{
		if (event instanceof MessageEvent)
		{
			try
			{
				this.incomingMessage(context, (MessageEvent) event);
			} catch (Throwable e)
			{
				Channels.fireExceptionCaught(context, e);
				log.error("Error handling a message event", e);
			}
		}
		else
		if (event instanceof ChannelEvent)
		{
			try
			{
				this.incomingChannelEvent(context, (ChannelEvent) event);
			} catch (Throwable e)
			{
				Channels.fireExceptionCaught(context, e);
				log.error("Error handling a channel event", e);
			}
		}
		else
		if (event instanceof ExceptionEvent)
		{
			try
			{
				incomingException(context, (ExceptionEvent) event);
			} catch (Throwable e)
			{
				// supress any errors
			}
		}
		context.sendUpstream(event);
	}

	@Override
	public void incomingException( ChannelHandlerContext context, ExceptionEvent event ) 
		throws Exception
	{
		//log.error("Incoming exception caught", event.getCause() );
	}
	
	@Override
	public void incomingMessage( ChannelHandlerContext context, MessageEvent event )
		throws Exception
	{

	}

	@Override
	public void incomingChannelEvent( ChannelHandlerContext context, ChannelEvent event )
		throws Exception
	{
	}
	
}
