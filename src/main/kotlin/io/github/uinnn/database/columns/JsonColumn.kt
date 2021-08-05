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

package io.github.uinnn.database.columns

import io.github.uinnn.serializer.DefaultJsonStrategySaveFormat
import io.github.uinnn.serializer.common.FrameworkModule
import kotlinx.serialization.serializer
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.StringColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.vendors.currentDialect
import kotlin.reflect.KClass

/**
 * A skeletal model for creating new column types using JSON.
 * @see JsonVarcharColumnType
 * @see JsonTextColumnType
 */
abstract class JsonColumnType<T : Any>(type: KClass<T>) : StringColumnType() {
  val serialType = FrameworkModule.serializer(type.java)

  override fun notNullValueToDB(value: Any) = DefaultJsonStrategySaveFormat.encodeToString(serialType, value as T)
  override fun nonNullValueToString(value: Any): String {
    val string = DefaultJsonStrategySaveFormat.encodeToString(serialType, value as T)
    return "'$string'"
  }

  override fun valueFromDB(value: Any): T = when (value) {
    is String -> DefaultJsonStrategySaveFormat.decodeFromString(serialType, value) as T
    else -> value as T
  }
}

/**
 * A [JsonVarcharColumnType] represents a column thats has your data as JSON.
 * The json data is VARCHAR, this is, as a limited amount of length.
 * If you need more length, use [JsonTextColumnType].
 */
open class JsonVarcharColumnType<T : Any>(val length: Int, type: KClass<T>) : JsonColumnType<T>(type) {
  override fun sqlType(): String = "VARCHAR($length)"
}

/**
 * A [JsonTextColumnType] represents a column thats has your data as JSON.
 * The json data is TEXT, this is, as a unlimited amount of length.
 */
open class JsonTextColumnType<T : Any>(type: KClass<T>) : JsonColumnType<T>(type) {
  override fun sqlType(): String = currentDialect.dataTypeProvider.textType()
}

/**
 * Registers a json VARCHAR column, with specified name, length
 * and type in this table.
 */
fun <T : Any> Table.json(name: String, length: Int, type: KClass<T>): Column<T> {
  return registerColumn(name, JsonVarcharColumnType(length, type))
}

/**
 * Registers a json TEXT column, with specified name and type
 * in this table.
 */
fun <T : Any> Table.json(name: String, type: KClass<T>): Column<T> {
  return registerColumn(name, JsonTextColumnType(type))
}

/**
 * Registers a json VARCHAR column, with specified name, length
 * and type in this table. This used reified generic type to get the [T] class.
 */
inline fun <reified T : Any> Table.json(name: String, length: Int): Column<T> {
  return registerColumn(name, JsonVarcharColumnType(length, T::class))
}

/**
 * Registers a json TEXT column, with specified name and type
 * in this table. This used reified generic type to get the [T] class.
 */
inline fun <reified T : Any> Table.json(name: String): Column<T> {
  return registerColumn(name, JsonTextColumnType(T::class))
}