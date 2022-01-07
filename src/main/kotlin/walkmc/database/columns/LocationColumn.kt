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

import kotlinx.serialization.modules.*
import org.bukkit.*
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*
import walkmc.serializer.common.*

/**
 * Represents a location column type thats is registered from a [JsonVarcharColumnType].
 */
class LocationColumnType(
	length: Int = 24,
	module: SerializersModule = FrameworkModule
) : JsonVarcharColumnType<Location>(length, Location::class, module)

/**
 * Represents a id table that their primary key is a location.
 */
abstract class LocationTable : IdTable<Location>() {
	override val id = location("id").entityId()
	override val primaryKey by lazy { PrimaryKey(id) }
}

/**
 * Represents a location database entity.
 */
abstract class LocationEntity(id: EntityID<Location>) : Entity<Location>(id)

/**
 * Represents a location database entity class.
 */
abstract class LocationEntityClass<T : Entity<Location>>(
	table: IdTable<Location>,
) : EntityClass<Location, T>(table)

/**
 * Registers a location column, with specified name in this table.
 */
fun Table.location(
	name: String,
	length: Int = 24,
	module: SerializersModule = FrameworkModule
) = registerColumn<Location>(name, LocationColumnType(length, module))
