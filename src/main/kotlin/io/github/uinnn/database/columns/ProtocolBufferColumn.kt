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

import io.github.uinnn.serializer.DefaultProtocolBufferStrategyFormat
import io.github.uinnn.serializer.common.FrameworkModule
import kotlinx.serialization.serializer
import org.jetbrains.exposed.sql.BasicBinaryColumnType
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.vendors.currentDialect
import kotlin.reflect.KClass

/**
 * A skeletal model for creating new column types using Protocol Buffers.
 * @see ProtocolBufferLimitedColumnType
 * @see ProtocolBufferUnlimitedColumnType
 */
abstract class ProtocolBufferColumnType<T : Any>(type: KClass<T>) : BasicBinaryColumnType() {
  val serialType = FrameworkModule.serializer(type.java)

  override fun notNullValueToDB(value: Any): ByteArray {
    return DefaultProtocolBufferStrategyFormat.encodeToByteArray(serialType, value as T)
  }

  override fun nonNullValueToString(value: Any): String {
    val string = DefaultProtocolBufferStrategyFormat.encodeToByteArray(serialType, value as T)
    return "'$string'"
  }

  override fun valueFromDB(value: Any): T = when (value) {
    is ByteArray -> DefaultProtocolBufferStrategyFormat.decodeFromByteArray(serialType, value) as T
    else -> value as T
  }
}

/**
 * A [ProtocolBufferLimitedColumnType] represents a column thats has your data
 * as Protocol Buffer. The protocol buffer data is a limited binary type, this is,
 * similar to [JsonVarcharColumnType] thats holds a specified length.
 * If you need more space use [ProtocolBufferUnlimitedColumnType].
 */
open class ProtocolBufferLimitedColumnType<T : Any>(
  val length: Int,
  type: KClass<T>
) : ProtocolBufferColumnType<T>(type) {
  override fun sqlType(): String = currentDialect.dataTypeProvider.binaryType(length)

  override fun validateValueBeforeUpdate(value: Any?) {
    if (value is ByteArray) {
      require(value.size <= length) {
        "Value '$value' can't be stored to database column because exceeds length ($length)"
      }
    }
  }
}

/**
 * A [ProtocolBufferUnlimitedColumnType] represents a column thats has your data
 * as Protocol Buffer. The protocol buffer data is a unlimited binary type, this is,
 * similar to [JsonTextColumnType] thats holds a unlimited length.
 */
open class ProtocolBufferUnlimitedColumnType<T : Any>(type: KClass<T>) : ProtocolBufferColumnType<T>(type)

/**
 * Registers a protocol buffer limited column, with specified name, length
 * and type in this table.
 */
fun <T : Any> Table.protobuf(name: String, length: Int, type: KClass<T>): Column<T> {
  return registerColumn(name, ProtocolBufferLimitedColumnType(length, type))
}

/**
 * Registers a protocol buffer unlimited column, with specified name and type
 * in this table.
 */
fun <T : Any> Table.protobuf(name: String, type: KClass<T>): Column<T> {
  return registerColumn(name, ProtocolBufferUnlimitedColumnType(type))
}

/**
 * Registers a protocol buffer limited column, with specified name, length
 * and type in this table. This used reified generic type to get the [T] class.
 */
inline fun <reified T : Any> Table.protobuf(name: String, length: Int): Column<T> {
  return registerColumn(name, ProtocolBufferLimitedColumnType(length, T::class))
}

/**
 * Registers a protocol buffer unlimited column, with specified name and type
 * in this table. This used reified generic type to get the [T] class.
 */
inline fun <reified T : Any> Table.protobuf(name: String): Column<T> {
  return registerColumn(name, ProtocolBufferUnlimitedColumnType(T::class))
}