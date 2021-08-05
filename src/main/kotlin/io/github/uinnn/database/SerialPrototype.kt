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


package io.github.uinnn.database

import io.github.uinnn.database.factory.PrototypeFactory
import kotlinx.serialization.Serializable
import java.io.File

/**
 * A serial prototype is a serializable class thats supports
 * to configure the database prototype settings.
 */
@Serializable
data class SerialPrototype(
  var type: DatabaseType = DatabaseType.SQLITE,
  var username: String = "root",
  var password: String = "",
  var host: String = "localhost",
  var database: String = "",
  var port: Int = 3306,
  var useSSL: Boolean = false
)

/**
 * Converts this [SerialPrototype] to a [LocalDatabasePrototype],
 * or throws a exception, if the type of this prototype is not a
 * local database prototype.
 */
fun SerialPrototype.asLocalPrototype(file: File): LocalDatabasePrototype {
  require(type == DatabaseType.SQLITE || type == DatabaseType.H2) {
    "The database type $type provided is not a local database prototype. Only 'SQLITE' and 'H2' is local."
  }
  return when (type) {
    DatabaseType.H2 -> PrototypeFactory.createH2Prototype(file)
    else -> PrototypeFactory.createSQLitePrototype(file)
  }
}

/**
 * Converts this [SerialPrototype] to a [LocalDatabasePrototype],
 * or throws a exception, if the type of this prototype is not a
 * local database prototype.
 */
fun SerialPrototype.asServerPrototype(): ServerDatabasePrototype {
  require(type == DatabaseType.MYSQL || type == DatabaseType.POSTGRE) {
    "The database type $type provided is not a server database prototype. Only 'MYSQL' and 'POSTGRE' is server."
  }
  return when (type) {
    DatabaseType.POSTGRE -> PrototypeFactory.createPostgrePrototype(this)
    else -> PrototypeFactory.createMySQLPrototype(this)
  }
}

/**
 * Converts this [SerialPrototype] to a [DatabasePrototype]. This is
 * used when the database prototype can be local or server. So this is
 * a abstraction for all supported types. A [file] can be required if
 * the prototype is a [LocalDatabasePrototype], by default is null.
 */
fun SerialPrototype.asPrototype(file: File? = null): DatabasePrototype {
  return when (type) {
    DatabaseType.SQLITE -> {
      requireNotNull(file) {
        "The database type $type is local, but no file is specified."
      }
      PrototypeFactory.createSQLitePrototype(file)
    }
    DatabaseType.H2 -> {
      requireNotNull(file) {
        "The database type $type is local, but no file is specified."
      }
      PrototypeFactory.createH2Prototype(file)
    }
    DatabaseType.POSTGRE -> PrototypeFactory.createPostgrePrototype(this)
    DatabaseType.MYSQL -> PrototypeFactory.createMySQLPrototype(this)
  }
}