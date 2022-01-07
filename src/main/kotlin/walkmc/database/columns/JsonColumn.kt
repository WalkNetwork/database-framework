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

package walkmc.database.columns

import kotlinx.serialization.*
import kotlinx.serialization.modules.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.vendors.*
import walkmc.extensions.*
import walkmc.serializer.*
import walkmc.serializer.common.*
import kotlin.reflect.*

/**
 * A skeletal model for creating new column types using JSON.
 * @see JsonVarcharColumnType
 * @see JsonTextColumnType
 */
abstract class JsonColumnType<T : Any>(
	type: KClass<T>,
	val module: SerializersModule = FrameworkModule
) : StringColumnType() {
	val serialType by lazy { module.serializer(type.java) }
	
	override fun notNullValueToDB(value: Any) = JsonStrategySave.encodeToString(serialType, value.cast())
	override fun nonNullValueToString(value: Any): String {
		val string = JsonStrategySave.encodeToString(serialType, value.cast())
		return "'$string'"
	}
	
	override fun valueFromDB(value: Any): T = when (value) {
		is String -> JsonStrategySave.decodeFromString(serialType, value).cast()
		else -> value.cast()
	}
}

/**
 * A [JsonVarcharColumnType] represents a column thats has your data as JSON.
 * The json data is VARCHAR, this is, as a limited amount of length.
 * If you need more length, use [JsonTextColumnType].
 */
open class JsonVarcharColumnType<T : Any>(
	val length: Int,
	type: KClass<T>,
	module: SerializersModule = FrameworkModule
) : JsonColumnType<T>(type, module) {
	override fun sqlType(): String = "VARCHAR($length)"
}

/**
 * A [JsonTextColumnType] represents a column thats has your data as JSON.
 * The json data is TEXT, this is, as a unlimited amount of length.
 */
open class JsonTextColumnType<T : Any>(
	type: KClass<T>,
	module: SerializersModule = FrameworkModule
) : JsonColumnType<T>(type, module) {
	override fun sqlType(): String = currentDialect.dataTypeProvider.textType()
}

/**
 * Registers a json VARCHAR column, with specified name, length
 * and type in this table. This used reified generic type to get the [T] class.
 */
inline fun <reified T : Any> Table.json(
	name: String,
	length: Int,
	module: SerializersModule = FrameworkModule
): Column<T> = registerColumn(name, JsonVarcharColumnType(length, T::class, module))


/**
 * Registers a json TEXT column, with specified name and type
 * in this table. This used reified generic type to get the [T] class.
 */
inline fun <reified T : Any> Table.json(
	name: String,
	module: SerializersModule = FrameworkModule
): Column<T> = registerColumn(name, JsonTextColumnType(T::class, module))

