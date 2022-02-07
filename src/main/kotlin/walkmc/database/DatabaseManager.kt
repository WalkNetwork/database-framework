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
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.jetbrains.exposed.sql.transactions.experimental.*
import walkmc.extensions.*

typealias SQLExpression = SqlExpressionBuilder.() -> Op<Boolean>

/**
 * A database manager is a manager for ID tables, thats uses
 * key-value pair to stores their data.
 *
 * [K] refers to the primary key used to get a value.
 *
 * [V] refers to the value of a key.
 *
 * [T] refers to the entity model of this table.
 */
interface IDatabaseManager<K, V, T : Entity<*>> {
	
	/**
	 * The coroutine scope owner of this database manager.
	 *
	 * Used to setup async tasks in this database.
	 */
	val scope: DatabaseScope
	
	/**
	 * The table of this manager.
	 */
	val table: Table
	
	/**
	 * The database of this manager.
	 */
	val database: Database
	
	/**
	 * Inserts a new value to this table with the specified key.
	 */
	fun insert(key: K, value: V): T
	
	/**
	 * Updates a value of this table by the specified key.
	 */
	fun update(key: K, value: V): T?
	
	/**
	 * Performs a operation that will update or inserts the specified
	 * key-value in this table.
	 *
	 * ### Note
	 * This function must be overrided, if performance is a factor.
	 */
	fun updateOrInsert(key: K, value: V): T {
		return if (key !in this) insert(key, value) else update(key, value)!!
	}
	
	/**
	 * Try finds a value by the specified key in this table,
	 * if not exists, will return null.
	 */
	fun find(key: K): T?
	
	/**
	 * Gets all contents of this table.
	 */
	fun all(): SizedIterable<T>
}

/**
 * A abstract implementation of [IDatabaseManager].
 * This is a skeletal model for creating others table managers.
 *
 * [K] refers to the primary key used to get a value.
 *
 * [V] refers to the value of a key.
 *
 * [T] refers to the entity model of this table.
 */
abstract class DatabaseManager<K, V, T : Entity<*>>(
	override val table: Table,
	override val database: Database,
	override val scope: DatabaseScope = DatabaseScope()
) : IDatabaseManager<K, V, T>

/**
 * Manages a management from [DatabaseScope] to inserts a
 * key-value in this table directly.
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.manageInsert(key: A, value: B): Job {
	return management {
		insert(key, value)
	}
}

/**
 * Manages a management from [DatabaseScope] to update
 * a key in this table directly.
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.manageUpdate(key: A, value: B): Job {
	return management {
		update(key, value)
	}
}

/**
 * Manages a management from [DatabaseScope] to update
 * or insert a key in this table directly.
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.manageUpdateOrInsert(key: A, value: B): Job {
	return management {
		updateOrInsert(key, value)
	}
}

/**
 * Deletes a key and their value in this table.
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.delete(key: A): C? {
	val entity = find(key)
	entity?.delete()
	return entity
}

/**
 * Manages a management from [DatabaseScope] to delete
 * a key in this table directly.
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.manageDelete(key: A): Job {
	return management {
		delete(key)
	}
}

/**
 * Deletes a key and their value in this table.
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.delete(user: C): C {
	user.delete()
	return user
}

/**
 * Manages a management from [DatabaseScope] to delete
 * a key in this table directly.
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.manageDelete(user: C): Job {
	return management {
		delete(user)
	}
}

/**
 * Gets a value by the specified key in this table,
 * if not exists, will throw a NPE.
 */
operator fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.get(key: A) = find(key)!!

/**
 * Gets a value by the specified key in this table as a [Result].
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.getAsResult(key: A): Result<C> {
	return runCatching {
		get(key)
	}
}

/**
 * Verify if in this table contains the specified key.
 */
operator fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.contains(key: A): Boolean =
	find(key) != null

/**
 * Deletes all key-value of this table and returns the amount
 * of deleted key-values.
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.deleteAll(): Int = table.deleteAll()

/**
 * Manages a management from [DatabaseScope] to delete all
 * contents of this table directly.
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.manageDeleteAll(): Job {
	return management {
		deleteAll()
	}
}

/**
 * Creates this table if not exists.
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.create() = SchemaUtils.create(table)

/**
 * Manages a management from [DatabaseScope] to create
 * this table if not exists.
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.manageCreate(): Job {
	return management {
		SchemaUtils.create(table)
	}
}

/**
 * Update all key-values from a specified map.
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.updateAll(map: Map<A, B>): List<C> {
	val list = ArrayList<C>()
	for ((key, value) in map)
		update(key, value)?.let { list.add(it) }
	return list
}

/**
 * Manages a management from [DatabaseScope] to update
 * all key-values from a specified map.
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.manageUpdateAll(map: Map<A, B>): Job {
	return management {
		updateAll(map)
	}
}

/**
 * Inserts all key-values from a specified map.
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.insertAll(map: Map<A, B>): List<C> {
	val list = ArrayList<C>()
	for ((key, value) in map)
		list.add(insert(key, value))
	return list
}

/**
 * Manages a management from [DatabaseScope] to inserts
 * all key-values from a specified map.
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.manageInsertAll(map: Map<A, B>): Job {
	return management {
		insertAll(map)
	}
}

/**
 * Update or inserts all key-values from a specified map.
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.updateOrInsertAll(map: Map<A, B>): List<C> {
	val list = ArrayList<C>()
	for ((key, value) in map)
		list.add(updateOrInsert(key, value))
	return list
}

/**
 * Manages a management from [DatabaseScope] to update
 * or insert all key-values from a specified map.
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.manageUpdateOrInsertAll(map: Map<A, B>): Job {
	return management {
		updateOrInsertAll(map)
	}
}

/**
 * Sorts ALL values from this table to the specified [comparator].
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.sortedWith(
	comparator: Comparator<C>,
) = all().sortedWith(comparator)

/**
 * Sorts ALL values from this table to the specified [selector].
 */
inline fun <A, B, C : Entity<*>, R : Comparable<R>> IDatabaseManager<A, B, C>.sortedBy(
	crossinline selector: (C) -> R,
) = all().sortedBy(selector)

/**
 * Sorts ALL values from this table to the specified [selector].
 */
inline fun <A, B, C : Entity<*>, R : Comparable<R>> IDatabaseManager<A, B, C>.sortedByDescending(
	crossinline selector: (C) -> R,
) = all().sortedByDescending(selector)

/**
 * Deletes all key-values from the specified sql expression.
 * You can also specify the limit and offset.
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.deleteIf(
	limit: Int? = null,
	offset: Long? = null,
	action: SQLExpression,
) = table.deleteWhere(limit, offset, action)

/**
 * Manages a management from [DatabaseScope] to delete all key-values
 * from the specified sql expression. You can also specify the limit and offset.
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.manageDeleteIf(
	limit: Int? = null,
	offset: Long? = null,
	action: SQLExpression,
) = management {
	deleteIf(limit, offset, action)
}


/**
 * Selects all key-values from this table and returns as query.
 */
fun <A, B, C : Entity<*>> IDatabaseManager<A, B, C>.selectAll(): Query = table.selectAll()

/**
 * Manages a [manage] in launch coroutine scope with this database.
 */
inline fun <T, A, B, C : Entity<*>, Z : IDatabaseManager<A, B, C>> Z.management(
	crossinline action: Z.(Transaction) -> T,
): Job {
	return scope.management(database) {
		action(this@management, this)
	}
}

/**
 * Manages a [manage] in launch coroutine scope with this database with start as lazy.
 */
inline fun <T, A, B, C : Entity<*>, Z : IDatabaseManager<A, B, C>> Z.lazyManagement(
	crossinline action: Z.(Transaction) -> T,
): Job {
	return scope.lazyManagement(database) {
		action(this@lazyManagement, this)
	}
}

/**
 * Manages a [manage] in async coroutine scope with this database.
 */
inline fun <T, A, B, C : Entity<*>, Z : IDatabaseManager<A, B, C>> Z.managementAsync(
	crossinline action: Z.(Transaction) -> T,
): Deferred<T> {
	return scope.managementAsync(database) {
		action(this@managementAsync, this)
	}
}

/**
 * Manages a [manage] in launch coroutine scope with this database with start as lazy.
 */
inline fun <T, A, B, C : Entity<*>, Z : IDatabaseManager<A, B, C>> Z.lazyManagementAsync(
	crossinline action: Z.(Transaction) -> T,
): Deferred<T> {
	return scope.lazyManagementAsync(database) {
		action(this@lazyManagementAsync, this)
	}
}

/**
 * Starts a transaction with the database of this table manager. The transaction is not async
 * and is specified by the default transaction [Transaction]
 */
inline fun <T, A, B, C : Entity<*>, Z : IDatabaseManager<A, B, C>> Z.transaction(
	crossinline action: Z.(Transaction) -> T,
): T {
	return transaction(database) {
		action(this@transaction, this)
	}
}
