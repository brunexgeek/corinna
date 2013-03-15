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
package corinna.thread;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

// TODO: move to 'corinna.util'
public class ObjectLocker
{

	private ReadLock readLock;
	
	private WriteLock writeLock;
	
	public ObjectLocker()
	{
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		readLock = lock.readLock();
		writeLock = lock.writeLock();
	}
	
	public void readLock()
	{
		readLock.lock();
	}

	public void readUnlock()
	{
		readLock.unlock();
	}
	
	public void writeLock()
	{
		writeLock.lock();
	}

	public void writeUnlock()
	{
		writeLock.unlock();
	}
	
	
}
