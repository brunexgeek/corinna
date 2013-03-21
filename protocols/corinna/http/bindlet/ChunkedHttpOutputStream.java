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
//package corinna.http.bindlet;
//
//
//import java.io.IOException;
//
//import javax.bindlet.IBindletResponse;
//
//import org.jboss.netty.handler.codec.http.DefaultHttpChunk;
//import org.jboss.netty.handler.codec.http.HttpChunk;
//
//
///**
// * Provides an output stream for sending binary data to the client through HTTP chunks.
// * 
// * @author Bruno Ribeiro
// * @version 1.0
// * @since 1.0
// * 
// * @see IBindletResponse
// */
////TODO: use this class as a generic chunked output stream
//public class ChunkedHttpOutputStream extends BufferedHttpOutputStream
//{
//
//	private HttpChunk chunk = null;
//	
//	public ChunkedHttpOutputStream( WebBindletResponse resp, int bufferSize )
//	{
//		super(resp, bufferSize);
//		chunk = new DefaultHttpChunk(buffer);
//	}
//
//	public ChunkedHttpOutputStream( WebBindletResponse resp )
//	{
//		this(resp, DEFAULT_BUFFER_SIZE);
//	}
//
//
//	@Override
//	public void flush() throws IOException
//	{
//		checkClosed();
//
//		if (!isCommited())
//		{
//			response.commit();
//			setCommited(true);
//		}
//
//		// ignore if the buffer has no data to flush (the last chunk will be sent by 'close' method)
//		if (!buffer.readable()) return;
//		
//		/*
//		 *  NOTE: the 'lastChunk' method of class 'HttpChunk' do not dynamically detects if the
//		 *  buffer has data. So we need to assign the buffer whenever the chunk is written. 
//		 */
//		chunk.setContent(buffer);
//		channel.write(chunk);
//		incWrittenBytes(buffer.writerIndex());
//		buffer.clear();
//	}
//
//	@Override
//	public void close() throws IOException
//	{
//		flush();
//
//		synchronized (isClosed)
//		{
//			channel.write(HttpChunk.LAST_CHUNK);
//			isClosed = true;
//		}
//	}
//	
//}
