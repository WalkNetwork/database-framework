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
 * Represents a world column type thats is registered from a [JsonVarcharColumnType].
 */
class WorldColumnType(
	module: SerializersModule = FrameworkModule
) : JsonVarcharColumnType<World>(12, World::class, module)

/**
 * Represents a id table that their primary key is a world.
 */
abstract class WorldTable : IdTable<World>() {
	override val id = world("id").entityId()
	override val primaryKey by lazy { PrimaryKey(id) }
}

/**
 * Represents a world database entity.
 */
abstract class WorldEntity(id: EntityID<World>) : Entity<World>(id)

/**
 * Represents a world database entity class.
 */
abstract class WorldEntityClass<T : Entity<World>>(
	table: IdTable<World>,
) : EntityClass<World, T>(table)

/**
 * Registers a world column, with specified name in this table.
 */
fun Table.world(name: String, module: SerializersModule = FrameworkModule) =
	registerColumn<World>(name, WorldColumnType(module))
