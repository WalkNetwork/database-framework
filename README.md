<a href="https://github.com/uinnn/database-framework">
  <img align="center" src="https://img.shields.io/static/v1?style=for-the-badge&label=author&message=uinnn&color=informational"/>
</a>
<a href="https://github.com/uinnn/database-framework">
  <img align="center" src="https://img.shields.io/static/v1?style=for-the-badge&label=version&message=1.3.10&color=yellow"/>
</a>

# database-framework

### Note:
> This repository is a showcase from WalkMC Network database-framework.
> This will not work in your server.

### Creating:
* MySQL
```kt
val storage = MySQL(username, password, database, host, port)
```

* SQLite
```kt
val storage = SQLite(file)
```

### Examples:

```kt
val db: Database = storage.database
val source: DataSource = storage.source
val config: HikariConfig = storage.config
```

### Serializing Database config
```kt
@Serializable
data class DatabaseStructure(val storage: SerialStorage = SerialStorage())
```

### Full working example:
```kt
@Serializable
data class Person(val name: String, val age: Int)

object PersonTable : IntTable() {
  val person = json<Person>(name = "person", length = 128)
}

class PersonEntity(id: EntityID<UUID>) : UUIDEntity(id) {
   companion object : UUIDEntityClass<PersonEntity>(PersonTable)
   
   var person by PersonTable.person
}

object PersonDatabase : DatabaseManager<UUID, Person, PersonEntity>(PersonTable, storage.database) {
   override fun all() = PersonEntity.all()
   override fun find(key: UUID) = PersonEntity.findById(key)
   override fun insert(key: UUID, value: Person) = PersonEntity.new(key) { person = value }
   
   override fun update(key: UUID, value: Person): PersonEntity? {
      val found = find(key) ?: return null
      return found.apply { person = value }
   }
   
   override fun updateOrInsert(key: UUID, value: Person): PersonEntity {
      val found = find(key)
      return found?.apply { person = value } ?: insert(key, value)
   }
```

### Examples:
```kt
PersonDatabase.management { // async transaction
  create() // create table
  all().forEach(::println) // fetch all data in table and print them
}
```

```kt
PersonDatabase.manageUpdateOrInsert(key = UUID.randomUUID(), value = Person(name = "Carrara", age = 18)) // inserts or update the value
```









