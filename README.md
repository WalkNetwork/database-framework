<a href="https://github.com/uinnn/database-framework">
  <img align="center" src="https://img.shields.io/static/v1?style=for-the-badge&label=author&message=uinnn&color=informational"/>
</a>
<a href="https://github.com/uinnn/database-framework">
  <img align="center" src="https://img.shields.io/static/v1?style=for-the-badge&label=version&message=1.0.2v&color=ff69b4"/>
</a>
<a href="https://github.com/uinnn/database-framework">
  <img align="center" src="https://img.shields.io/static/v1?style=for-the-badge&label=maven-central&message=1.0.2&color=orange"/>
</a>
<a href="https://github.com/uinnn/database-framework">
  <img align="center" src="https://img.shields.io/static/v1?style=for-the-badge&label=license&message=MIT License&color=success"/>
</a>

# database-framework
A lightweight and asynchronous kotlin database framework using Kotlin Exposed and HikariCP for spigot/standalone.

### Objective 📝
The main purpose of `database-framework` is to create pre-configured database prototypes (models) for use, using HikariCP.
It also suits with an asynchronously easy transaction using Kotlin Coroutines.

### How To Use
#### You can see the dokka documentation [here](https://uinnn.github.io/database-framework/)

### Prototypes
You can create a prototype by one of four pré-configured prototypes used in databases:

* MySQLPrototype 
> (server database prototype)
* PostgrePrototype 
> (server database prototype)
* SQLitePrototype 
> (local database prototype)
* H2Prototype 
> (local database prototype)

### Local database prototypes
Local database prototypes is prototypes thats `not` uses connection, such as SQLite and H2, both uses files.

### Server database prototypes
Server database prototypes is prototypes thats uses and opens connections to a server, such as MySQL and PostgreSQL.

### Creating prototypes
To create a prototype is very simple, just call a prototype factory:

* MySQL
```kt
val mysqlPrototype = PrototypeFactory.createMySQLPrototype(username, password, databaseName, host, port, useSSL)
```

* PostgreSQL
```kt
val postgrePrototype = PrototypeFactory.createPostgrePrototype(username, password, databaseName, host, port, useSSL)
```

* SQLite
```kt
val sqlitePrototype = PrototypeFactory.createSQLitePrototype(file)
```

* H2
```kt
val h2Prototype = PrototypeFactory.createH2Prototype(file)
```

### Getting the database of a prototype
All prototypes accompany a database:
> Note that all database is lazy init, so you dont need to create a lazy init for caching the database. This makes the work for you.
```kt
val database = prototype.database
```

Other functions:
```kt
val source = prototype.source // returns a DataSource
```

```kt
val config = prototype.config // returns a HikariConfig.
```

```kt
val type = prototype.type // returns a database type (sqlite, mysql, h2 or postgre)
```

### Serial Prototype
A serial prototype is a serializable prototype thats automatically creates a database prototype by a configuration:
```kt
val serial = SerialPrototype() // default parameters = sqlite database
```

All supported parameters:
```kt
val serial = SerialPrototype(
  type = DatabaseType.MYSQL,
  username = "root",
  password = "secret",
  host = "localhost",
  database = "myDatabaseName",
  port = 3306,
  useSSL = false
)
```

### Examples of use with serial prototype
```kt
@Serializable
data class DatabaseSettings(val prototype: SerialPrototype = SerialPrototype())
```

Now you can use like another one serializable class!

### Converting a serial prototype to a database prototype
Now you have a serial prototype you must want to convert to a database prototype. Some examples:
```kt
// must require thats the database type specified in the serial is 'SQLITE' or 'H2'
val prototype = serial.asLocalPrototype(file) 
```

```kt
// must require thats the database type specified in the serial is 'MYSQL' or 'POSTGRE'
val prototype = serial.asServerPrototype() 
```

```kt
// a abstract implementation to convert to database prototype, here, the prototype can be local or a server prototype.
// a file parameter is specified to initialize IF the prototype is a local, if not, the file is not used.
val prototype = serial.asPrototype(file = null) 
```

### New columns
The `database-framework` comes with new columns supporteds:
> Note thats to use the new columns, you will need to use the [serializer-framework](https://github.com/uinnn/serializer-framework)

Let's suppose I have a serializable template class:
```kt
@Serializable
data class Model(
  var name: String = "uinnn",
  var amount: Int = 0,
  var metadata: Map<String, String> = HashMap()
)
```

To use the news columns:
> Note you need to put the function inside a `Table` class.
* Json columns
```kt
// will create a VARCHAR sql type with 64 length and serialize/deserialize the Model class.
json("jsonData", 64, Model::class)
```

```kt
// will create a TEXT sql type and serialize/deserialize the Model class.
json("jsonData", Model::class)
```

* Protocol buffer columns
```kt
// will create a VARBINARY sql type with 64 length and serialize/deserialize the Model class.
protobuf("protobufData", 64, Model::class)
```

```kt
// will create a unlimited binary length sql type and serialize/deserialize the Model class.
protobuf("protobufData", Model::class)
```

### Table manager
A table manager is a manager of key-value tables, used to easily handler of them. Also asynchronously functions is implemented.

Let's suppose I have the following objects
```kt
object UserTable : IntIdTable() {
  val data = protobuf("model", Model::class)
}

class User(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<User>(UserTable)
  var data by UserTable.data
}

// Generic parameters:
// Int - represents the key
// Model - represents the value
// User - represents the entity
object UserTableManager : AbstractTableManager<Int, Model, User>(table = UserTable, database = database) {
  override fun insert(key: Int, value: Model) {
    User.new(key) {
      data = value
    }
  }

  override fun update(key: Int, value: Model) {
    find(key)?.data = value
  }

  override fun find(key: Int): User? {
    return User.findById(key)
  }

  override fun all(): SizedIterable<User> {
    return User.all()
  }
}
```

Following this model we can already use the functions thats `TableManager` provides:
> Note that some functions of this DONT opens a transaction scope, this is like a 'model'
> but, some functions thats opens a transaction scope is specified by 'manage'
> The reason for this is to provide greater freedom when providing your own transaction scopes.

* Creating the table:
```kt
// not really creates, only model
UserTableManager.create()
// creates the table, opening the transaction scope asynchronously
UserTableManager.manageCreate()
```

* Deleting a key:
```kt
UserTableManager.delete(key)
UserTableManager.manageDelete(key)
```

* Inserting:
```kt
UserTableManager.insert(key, value)
UserTableManager.manageInsert(key, value)
```

* Deleting all:
```kt
UserTableManager.deleteAll()
UserTableManager.manageDeleteAll()
```

* Update:
```kt
UserTableManager.update(key, value)
UserTableManager.manageUpdate(key, value)
```

* Update or insert:
```kt
UserTableManager.updateOrInsert(key, value)
UserTableManager.manageUpdateOrInsert(key, value)
```

* Update or insert all:
```kt
val map = Map<K, V> = mapOf(key1 to value1, key2 to value2)
UserTableManager.updateOrInsertAll(map)
UserTableManager.manageUpdateOrInsertAll(map)
```

### Asynchronously transactions
To use asynchronously transactions is very simple:
```kt
database.management { // asynchronous transaction scope
  UserTableManager.create() // creates the table
  // inserts 10 entity in the database
  repeat(10) {
    UserTableManager.insert(it, Model())
  }
}
```

### Lazy management
The lazy management is very similar to the default management, but this will not be executed until a start function is called.
```kt
val management = database.lazyManagement { // asynchronous transaction scope
  UserTableManager.create() // creates the table
  // inserts 10 entity in the database
  repeat(10) {
    UserTableManager.insert(it, Model())
  }
}

// starts the management
management.start()
```

---

## Setup for development
The `database-framework` is in the central maven repository. Thus making things very easy!

### Gradle Kotlin DSL

```gradle
implementation("io.github.uinnn:database-framework:1.0.2")
```

### Gradle
```gradle
implementation 'io.github.uinnn:database-framework:1.0.2'
```

### Maven

```xml
<dependency>
  <groupId>io.github.uinnn</groupId>
  <artifactId>database-framework</artifactId>
  <version>1.0.2</version>
</dependency>
```

### Final notes
The `database-framework` **NOT** contains the kotlin runtime, kotlin serialization and others needed to run this framework,
so you should implement them directly in your project.
To make your life easier, here is all the implementation of the libraries needed to run the framework:

```gradle
plugins {
  kotlin("jvm") version "1.5.21"
  kotlin("plugin.serialization") version "1.5.21"
}

dependencies {
  implementation(kotlin("stdlib-jdk8")) // the kotlin std lib with jdk8 
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1") // the kotlin coroutines used to asynchronously manage
  
  implementation("io.github.uinnn:serializer-framework:1.6.2") // the serializer framework used to use new column types
  
  // the following dependencies is marked as API by the database-framework:
  api("com.zaxxer:HikariCP:4.0.3") // the hikariCP version 4.0.3
  api("org.jetbrains.exposed:exposed-core:0.32.1") // the kotlin exposed core version 0.32.1
  api("org.jetbrains.exposed:exposed-dao:0.32.1") // the kotlin exposed dao version 0.32.1
  api("org.jetbrains.exposed:exposed-jdbc:0.32.1") // the kotlin exposed jdbc version 0.32.1
  api("com.h2database:h2:1.4.200") // the h2 database provider
  api("org.xerial:sqlite-jdbc:3.36.0.1") // the updated sqlite database provider
  api("org.postgresql:postgresql:42.2.16") // the postgresql database provider
}
```








