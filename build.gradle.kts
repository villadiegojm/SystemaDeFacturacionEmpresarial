plugins {
    kotlin("jvm") version "2.0.21"
    id("nu.studer.jooq") version "9.0"
}

group = "com.jmvn.proyectos"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("org.jooq:jooq:3.20.4")
    jooqGenerator("org.xerial:sqlite-jdbc:3.40.0.0")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

jooq {
    configurations {
        create("main") {
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.sqlite.JDBC"
                    url = "jdbc:sqlite:SystemaFacturacion.db"  // ruta a tu archivo .db
                    user = ""
                    password = ""
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.sqlite.SQLiteDatabase"
                        inputSchema = ""  // SQLite no usa schemas
                    }
                    target.apply {
                        packageName = "com.jmvn.proyectos.tables"
                        directory = "build/generated-src/jooq/main"
                    }
                }
            }
        }
    }
}