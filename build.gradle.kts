plugins {
    kotlin("jvm") version "2.0.21"
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

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}