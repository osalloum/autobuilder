plugins {
    kotlin("jvm")
    id("maven-publish")
    alias(libs.plugins.ksp)
}

group = "io.github.mattshoe.shoebox.autobuilder"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(project(":AutoBuilder.Annotations"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}