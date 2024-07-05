plugins {
    kotlin("jvm")
    id("maven-publish")
}

group = "io.github.mattshoe.shoebox.autobuilder"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}