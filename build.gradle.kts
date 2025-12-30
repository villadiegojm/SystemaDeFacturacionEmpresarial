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
    implementation("com.mysql:mysql-connector-j:9.5.0")
    implementation("org.jooq:jooq:3.20.4")
    jooqGenerator("com.mysql:mysql-connector-j:9.5.0")
    implementation("org.slf4j:slf4j-api:2.0.17")

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
                    driver = "com.mysql.cj.jdbc.Driver"
                    url = "jdbc:mysql://localhost:3306/sys_facturacion_empresarial" + // ruta a tu archivo .db
                          "?useSSL=false" +
                          "&serverTimezone=UTC" +
                          "&allowPublicKeyRetrieval=true"
                    user = "root"
                    password = "Juanmideveloper@2024"
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.mysql.MySQLDatabase"
                        inputSchema = "sys_facturacion_empresarial"
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

sourceSets {
    main {
        kotlin.srcDir("build/generated-src/jooq/main")
    }
}
