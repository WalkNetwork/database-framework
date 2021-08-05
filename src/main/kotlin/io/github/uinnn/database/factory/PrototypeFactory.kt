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

package io.github.uinnn.database.factory

import io.github.uinnn.database.*
import java.io.File

/**
 * A factory object used to create new databases prototypes.
 */
object PrototypeFactory {

  /**
   * Creates a new [SQLitePrototype] by the specified file.
   */
  fun createSQLitePrototype(file: File): LocalDatabasePrototype = SQLitePrototype(file)

  /**
   * Creates a new [H2Prototype] by the specified file.
   */
  fun createH2Prototype(file: File): LocalDatabasePrototype = H2Prototype(file)

  /**
   * Creates a new [MySQLPrototype] by the specifieds parameters
   * to connect to the server database.
   */
  fun createMySQLPrototype(
    username: String,
    password: String,
    databaseName: String,
    host: String,
    port: Int = 3306,
    useSSL: Boolean = false
  ): ServerDatabasePrototype = MySQLPrototype(username, password, databaseName, host, port, useSSL)

  /**
   * Creates a new [PostgrePrototype] by the specifieds parameters
   * to connect to the server database.
   */
  fun createPostgrePrototype(
    username: String,
    password: String,
    databaseName: String,
    host: String,
    port: Int = 3306,
    useSSL: Boolean = false
  ): ServerDatabasePrototype = PostgrePrototype(username, password, databaseName, host, port, useSSL)

  /**
   * Creates a new [MySQLPrototype] by the serial prototype
   * parameters to connect to the server database.
   */
  fun createMySQLPrototype(serial: SerialPrototype): ServerDatabasePrototype {
    return MySQLPrototype(
      serial.username,
      serial.password,
      serial.database,
      serial.host,
      serial.port,
      serial.useSSL
    )
  }

  /**
   * Creates a new [PostgrePrototype] by the serial prototype
   * parameters to connect to the server database.
   */
  fun createPostgrePrototype(serial: SerialPrototype): ServerDatabasePrototype {
    return PostgrePrototype(
      serial.username,
      serial.password,
      serial.database,
      serial.host,
      serial.port,
      serial.useSSL
    )
  }
}