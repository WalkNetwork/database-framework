plugins {
  kotlin("jvm") version "1.5.21"
  kotlin("plugin.serialization") version "1.5.21"
  id("java")
  id("com.github.johnrengelman.shadow") version "6.0.0"
  id("maven-publish")
  id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
  id("signing")
  id("org.jetbrains.dokka") version "1.5.0"
}

group = "io.github.uinnn"
version = "1.0.3"

repositories {
  mavenCentral()
  mavenLocal()
}

dependencies {
  compileOnly("io.github.uinnn:serializer-framework:1.6.2")
  compileOnly("com.single.api:spigot:1.8.9")
  compileOnly(kotlin("stdlib-jdk8"))
  compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
  compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core:1.2.2")
  api("com.zaxxer:HikariCP:4.0.3")
  api("org.jetbrains.exposed:exposed-core:0.32.1")
  api("org.jetbrains.exposed:exposed-dao:0.32.1")
  api("org.jetbrains.exposed:exposed-jdbc:0.32.1")
  api("com.h2database:h2:1.4.200")
  api("org.xerial:sqlite-jdbc:3.36.0.1")
  api("org.postgresql:postgresql:42.2.16")
}

nexusPublishing {
  repositories {
    sonatype {
      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
    }
  }
}

tasks {
  publishing {
    repositories {
      maven {
        url = uri("https://repo.maven.apache.org/maven2/")
      }
    }
    publications {
      create<MavenPublication>("maven") {
        from(project.components["kotlin"])

        val sourcesJar by creating(Jar::class) {
          archiveClassifier.set("sources")
          from(sourceSets.main.get().allSource)
        }

        val javadocJar by creating(Jar::class) {
          dependsOn.add(javadoc)
          archiveClassifier.set("javadoc")
          from(javadoc)
        }

        setArtifacts(listOf(sourcesJar, javadocJar, jar))

        groupId = "io.github.uinnn"
        artifactId = "database-framework"
        version = project.version.toString()
        pom {
          name.set("database-framework")
          description.set("A lightweight and asynchronous kotlin database framework using Kotlin Exposed and HikariCP for spigot.")
          url.set("https://github.com/uinnn/database-framework")
          developers {
            developer {
              id.set("uinnn")
              name.set("Uin Carrara")
              email.set("uin.carrara@gmail.com")
            }
          }
          licenses {
            license {
              name.set("MIT Licenses")
            }
          }
          scm {
            url.set("https://github.com/uinnn/database-framework/tree/master/src")
          }
        }
      }
    }
  }

  signing {
    sign(publishing.publications["maven"])
  }

  compileKotlin {
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime," +
        "kotlin.ExperimentalStdlibApi," +
        "kotlinx.coroutines.DelicateCoroutinesApi," +
        "kotlinx.coroutines.ExperimentalCoroutinesApi," +
        "kotlinx.serialization.ExperimentalSerializationApi," +
        "kotlinx.serialization.InternalSerializationApi"
      }

  shadowJar {
    destinationDir = file("C:\\Users\\Cliente\\Minecraft\\Local\\plugins")
    archiveName = "${project.name}.jar"
    baseName = project.name
    version = project.version.toString()
  }
}