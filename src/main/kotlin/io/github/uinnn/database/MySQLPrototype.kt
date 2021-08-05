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

/**
 * A server database prototype for working with MySQL databases.
 */
class MySQLPrototype(
  username: String,
  password: String,
  databaseName: String,
  host: String,
  port: Int = 3306,
  useSSl: Boolean = false
) : AbstractServerDatabasePrototype(DatabaseType.MYSQL, username, password, databaseName, host, port, useSSl) {
  override val config: HikariConfig = ConfigurationFactory.of(DatabaseType.MYSQL) {
    jdbcUrl = "jdbc:mysql://$host:$port/$databaseName?useSSL=$useSSl"
    this.username = username
    this.password = password
    isAutoCommit = false
    addDataSourceProperty("cachePrepStmts", "true")
    addDataSourceProperty("prepStmtCacheSize", "350")
    addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
    addDataSourceProperty("useServerPrepStmts", "true")
    addDataSourceProperty("createDatabaseIfNotExist", "true")
  }
}