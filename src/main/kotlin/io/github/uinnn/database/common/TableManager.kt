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

package io.github.uinnn.database.common

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.sql.*

typealias SQLExpression = SqlExpressionBuilder.() -> Op<Boolean>

/**
 * A table manager is a manager for ID tables, thats uses
 * key-value pair to stores their data.
 * [K] refers to the primary key used to get a value.
 * [V] refers to the value of a key.
 * [T] refers to the entity model of this table.
 */
interface TableManager<K, V, T : Entity<*>> {

  /**
   * The table of this manager.
   */
  val table: Table

  /**
   * The database owner of this table manager.
   */
  val database: Database

  /**
   * Inserts a new value to this table with the specified key.
   */
  fun insert(key: K, value: V)

  /**
   * Updates a value of this table by the specified key.
   */
  fun update(key: K, value: V)

  /**
   * Performs a operation that will update or inserts the specified
   * key-value in this table.
   * ### Note
   * This function must be overrided, if performance is a factor.
   */
  fun updateOrInsert(key: K, value: V) {
    if (key !in this) insert(key, value) else update(key, value)
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
 * A abstract implementation of [TableManager].
 * This is a skeletal model for creating others table managers.
 */
abstract class AbstractTableManager<K, V, T : Entity<*>>(
  override val table: Table,
  override val database: Database
) : TableManager<K, V, T>

/**
 * Manages a management from [DatabaseScope] to inserts a
 * key-value in this table directly.
 */
fun <K, V> TableManager<K, V, *>.manageNew(key: K, value: V) {
  database.management {
    insert(key, value)
  }
}

/**
 * Manages a management from [DatabaseScope] to update
 * a key in this table directly.
 */
fun <K, V> TableManager<K, V, *>.manageUpdate(key: K, value: V) {
  database.management {
    update(key, value)
  }
}

/**
 * Manages a management from [DatabaseScope] to update
 * or insert a key in this table directly.
 */
fun <K, V> TableManager<K, V, *>.manageUpdateOrInsert(key: K, value: V) {
  database.management {
    updateOrInsert(key, value)
  }
}

/**
 * Deletes a key and their value in this table.
 */
fun <K> TableManager<K, *, *>.delete(key: K) {
  find(key)?.delete()
}

/**
 * Manages a management from [DatabaseScope] to delete
 * a key in this table directly.
 */
fun <K> TableManager<K, *, *>.manageDelete(key: K) {
  database.management {
    delete(key)
  }
}

/**
 * Gets a value by the specified key in this table,
 * if not exists, will throw a NPE.
 */
operator fun <K, T : Entity<*>> TableManager<K, *, T>.get(key: K) = find(key)!!

/**
 * Gets a value by the specified key in this table as a [Result].
 */
fun <K, T : Entity<*>> TableManager<K, *, T>.getAsResult(key: K): Result<T> {
  return runCatching {
    get(key)
  }
}

/**
 * Verify if in this table contains the specified key.
 */
operator fun <K> TableManager<K, *, *>.contains(key: K): Boolean = find(key) != null

/**
 * Deletes all key-value of this table and returns the amount
 * of deleted key-values.
 */
fun TableManager<*, *, *>.deleteAll(): Int = table.deleteAll()

/**
 * Manages a management from [DatabaseScope] to delete all
 * contents of this table directly.
 */
fun TableManager<*, *, *>.manageDeleteAll() {
  database.management {
    deleteAll()
  }
}

/**
 * Creates this table if not exists.
 */
fun TableManager<*, *, *>.create() = SchemaUtils.create(table)

/**
 * Manages a management from [DatabaseScope] to create
 * this table if not exists.
 */
fun TableManager<*, *, *>.manageCreate() {
  database.management {
    SchemaUtils.create(table)
  }
}

/**
 * Update all key-values from a specified map.
 */
fun <K, V> TableManager<K, V, *>.updateAll(map: Map<K, V>) {
  for ((key, value) in map)
    update(key, value)
}

/**
 * Manages a management from [DatabaseScope] to update
 * all key-values from a specified map.
 */
fun <K, V> TableManager<K, V, *>.manageUpdateAll(map: Map<K, V>) {
  database.management {
    updateAll(map)
  }
}

/**
 * Inserts all key-values from a specified map.
 */
fun <K, V> TableManager<K, V, *>.insertAll(map: Map<K, V>) {
  for ((key, value) in map)
    insert(key, value)
}

/**
 * Manages a management from [DatabaseScope] to inserts
 * all key-values from a specified map.
 */
fun <K, V> TableManager<K, V, *>.manageInsertAll(map: Map<K, V>) {
  database.management {
    insertAll(map)
  }
}

/**
 * Update or inserts all key-values from a specified map.
 */
fun <K, V> TableManager<K, V, *>.updateOrInsertAll(map: Map<K, V>) {
  for ((key, value) in map)
    updateOrInsert(key, value)
}

/**
 * Manages a management from [DatabaseScope] to update
 * or insert all key-values from a specified map.
 */
fun <K, V> TableManager<K, V, *>.manageUpdateOrInsertAll(map: Map<K, V>) {
  database.management {
    updateOrInsertAll(map)
  }
}

/**
 * Deletes all key-values from the specified sql expression.
 * You can also specify the limit and offset.
 */
fun TableManager<*, *, *>.deleteIf(limit: Int? = null, offset: Long? = null, action: SQLExpression): Int {
  return table.deleteWhere(limit, offset, action)
}

/**
 * Manages a management from [DatabaseScope] to delete all key-values
 * from the specified sql expression. You can also specify the limit and offset.
 */
fun TableManager<*, *, *>.manageDeleteIf(limit: Int? = null, offset: Long? = null, action: SQLExpression) {
  database.management {
    deleteIf(limit, offset, action)
  }
}

/**
 * Selects all key-values from this table and returns as query.
 */
fun TableManager<*, *, *>.selectAll(): Query = table.selectAll()



