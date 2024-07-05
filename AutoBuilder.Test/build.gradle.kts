plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "2.0.0-1.0.21"
}

group = "io.github.mattshoe.shoebox.autobuilder"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
//    ksp(project(":AutoBuilder.Processor"))
    implementation(project(":AutoBuilder.Annotations"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}