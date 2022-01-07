package walkmc.database.columns

import kotlinx.serialization.modules.*
import org.bukkit.block.*
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*
import walkmc.serializer.common.*

/**
 * Represents a block column type thats is registered from a [JsonVarcharColumnType].
 */
class BlockColumnType(
	length: Int = 24,
	module: SerializersModule = FrameworkModule
) : JsonVarcharColumnType<Block>(length, Block::class, module)

/**
 * Represents a id table that their primary key is a block.
 */
abstract class BlockTable : IdTable<Block>() {
	override val id = block("id").entityId()
	override val primaryKey by lazy { PrimaryKey(id) }
}

/**
 * Represents a block database entity.
 */
abstract class BlockEntity(id: EntityID<Block>) : Entity<Block>(id)

/**
 * Represents a block database entity class.
 */
abstract class BlockEntityClass<T : Entity<Block>>(
	table: IdTable<Block>,
) : EntityClass<Block, T>(table)

/**
 * Registers a block column, with specified name in this table.
 */
fun Table.block(name: String, length: Int = 24, module: SerializersModule = FrameworkModule) =
	registerColumn<Block>(name, BlockColumnType(length, module))
