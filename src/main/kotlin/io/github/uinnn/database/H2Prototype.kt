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
import io.github.uinnn.database.factory.ConfigurationFactory
import java.io.File

/**
 * A local database prototype for working with H2 databases.
 */
class H2Prototype(file: File) : AbstractLocalDatabasePrototype(DatabaseType.H2, file) {
  override val config: HikariConfig = ConfigurationFactory.of(DatabaseType.H2) {
    jdbcUrl = "jdbc:h2:./${file.path}"
    isAutoCommit = false
    connectionTestQuery = "SELECT 1"
    transactionIsolation = "TRANSACTION_SERIALIZABLE"
  }
}