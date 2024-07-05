plugins {
    kotlin("jvm")
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