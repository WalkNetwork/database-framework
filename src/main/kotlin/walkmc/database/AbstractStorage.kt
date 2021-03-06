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

import com.zaxxer.hikari.*
import org.jetbrains.exposed.sql.*
import java.io.*
import javax.sql.*

/**
 * A abstract implementation of [Storage].
 * This is a skeletal model for creating new database prototypes.
 */
abstract class AbstractStorage(override val type: DatabaseType) : Storage {
	override val source: DataSource by lazy { HikariDataSource(config) }
	override val database: Database by lazy { Database.connect(source) }
}

/**
 * A abstract implementation of [LocalStorage].
 * This is a skeletal model for creating new local database prototypes.
 */
abstract class AbstractLocalStorage(
	type: DatabaseType,
	override var file: File,
) : AbstractStorage(type), LocalStorage {
	init {
		file.parentFile?.mkdirs()
		if (type == DatabaseType.SQLITE && !file.exists()) {
			file.createNewFile()
		}
	}
}

/**
 * A abstract implementation of [ServerStorage].
 * This is a skeletal model for creating new server database prototypes.
 */
abstract class AbstractServerStorage(
	type: DatabaseType,
	override var username: String,
	override var password: String,
	override var databaseName: String,
	override var host: String,
	override var port: Int = 3306,
	override var useSSl: Boolean = false,
) : AbstractStorage(type), ServerStorage
