/*
                             MIT License

                        Copyright (c) 2021 uin

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package walkmc.database

import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import kotlin.coroutines.*

typealias Management<T> = Transaction.() -> T

/**
 * A database scope is a coroutine scope thats executes
 * database transactions asynchronously.
 */
class DatabaseScope : CoroutineScope {
	override val coroutineContext: CoroutineContext = EmptyCoroutineContext
	
	/**
	 * Suspend function to manage database transactions in asynchronously.
	 */
	suspend fun <T> manage(database: Database, action: Management<T>): T {
		return withContext(Dispatchers.Default) {
			transaction(database, action)
		}
	}
	
	/**
	 * Manages a [manage] in launch coroutine scope.
	 */
	fun <T> management(database: Database, action: Management<T>): Job {
		return launch {
			manage(database, action)
		}
	}
	
	/**
	 * Manages a [manage] in launch coroutine scope with start as lazy.
	 */
	fun <T> lazyManagement(database: Database, action: Management<T>): Job {
		return launch(start = CoroutineStart.LAZY) {
			manage(database, action)
		}
	}
	
	/**
	 * Manages a [manage] in async coroutine scope.
	 */
	fun <T> managementAsync(database: Database, action: Management<T>): Deferred<T> {
		return async {
			manage(database, action)
		}
	}
	
	/**
	 * Manages a [manage] in async coroutine scope with start as lazy.
	 */
	fun <T> lazyManagementAsync(database: Database, action: Management<T>): Deferred<T> {
		return async(start = CoroutineStart.LAZY) {
			manage(database, action)
		}
	}
}
