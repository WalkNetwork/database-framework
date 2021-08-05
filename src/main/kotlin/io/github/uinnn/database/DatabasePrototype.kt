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

import com.zaxxer.hikari.HikariConfig
import org.jetbrains.exposed.sql.Database
import java.io.File
import javax.sql.DataSource

/**
 * Represents a database prototype, this is, a model for creating
 * custom databases, with custom data sources, configurations and more.
 */
interface DatabasePrototype {

  /**
   * The type of this database prototype.
   */
  val type: DatabaseType

  /**
   * The [HikariConfig] configuration of this database prototype.
   */
  val config: HikariConfig

  /**
   * The [DataSource] source of this database prototype.
   */
  val source: DataSource

  /**
   * The final database of this database prototype.
   */
  val database: Database
}

/**
 * A local database implementation for use with [DatabasePrototype].
 */
interface LocalDatabasePrototype : DatabasePrototype {

  /**
   * The local file used in this local database prototype.
   */
  var file: File
}

/**
 * A server database implementation for use with [DatabasePrototype]
 */
interface ServerDatabasePrototype : DatabasePrototype {

  /**
   * The username of this server database prototype.
   */
  var username: String

  /**
   * The password of this server database prototype.
   */
  var password: String

  /**
   * The host of this server database prototype.
   */
  var host: String

  /**
   * The port of this server database prototype.
   */
  var port: Int

  /**
   * The database name of this server database prototype.
   */
  var databaseName: String

  /**
   * If this server database prototype uses secure socket layer in connections.
   */
  var useSSl: Boolean
}