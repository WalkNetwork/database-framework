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

package walkmc.database.extensions

import org.jetbrains.exposed.sql.*
import walkmc.database.*

/**
 * Manages a [action] in launch coroutine scope.
 */
fun <T> Database.management(action: Management<T>) = DatabaseScope.management(this, action)

/**
 * Manages a [action] in launch coroutine scope with start as lazy.
 */
fun <T> Database.lazyManagement(action: Management<T>) = DatabaseScope.lazyManagement(this, action)

/**
 * Manages a [action] in async coroutine scope.
 */
fun <T> Database.managementAsync(action: Management<T>) = DatabaseScope.managementAsync(this, action)

/**
 * Manages a [action] in launch coroutine scope with start as lazy.
 */
fun <T> Database.lazyManagementAsync(action: Management<T>) = DatabaseScope.lazyManagementAsync(this, action)