plugins {
    kotlin("jvm")
    id("maven-publish")
    signing
}

val GROUP_ID: String = project.properties["group.id"].toString()
val VERSION: String = project.properties["version"].toString()
val ARTIFACT_ID = "AutoBuilder.Processor"
val PUBLICATION_NAME = "autoBuilderProcessor"

group = GROUP_ID
version = VERSION

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(project(":AutoBuilder.Annotations"))
    implementation(kotlin("stdlib"))
    implementation("com.squareup:kotlinpoet:1.17.0")
    implementation("com.squareup:kotlinpoet-ksp:1.17.0")
    implementation("com.google.devtools.ksp:symbol-processing-api:2.0.0-1.0.21")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.6.0")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.5.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(19)
}

publishing {
    publications {
        repositories {
            mavenLocal()
        }

        create<MavenPublication>(PUBLICATION_NAME) {
            from(components["java"])
            groupId = GROUP_ID
            artifactId = ARTIFACT_ID
            version = VERSION
            pom {
                name = "AutoBuilder.Processor"
                description = "AutoBuilder: A Kotlin Symbol Processing (KSP) library for automatic builder class generation. Supports default values for both primitive and non-primitive properties."
                url = "https://github.com/mattshoe/autobuilder"
                properties = mapOf(
                    "myProp" to "value"
                )
                packaging = "aar"
                inceptionYear = "2024"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "mattshoe"
                        name = "Matthew Shoemaker"
                        email = "mattshoe81@gmail.com"
                    }
                }
                scm {
                    connection = "scm:git:git@github.com:mattshoe/autobuilder.git"
                    developerConnection = "scm:git:git@github.com:mattshoe/autobuilder.git"
                    url = "https://github.com/mattshoe/autobuilder"
                }
            }
        }


        signing {
            val signingKey = providers
                .environmentVariable("GPG_SIGNING_KEY")
                .forUseAtConfigurationTime()
            val signingPassphrase = providers
                .environmentVariable("GPG_SIGNING_PASSPHRASE")
                .forUseAtConfigurationTime()
            if (signingKey.isPresent && signingPassphrase.isPresent) {
                useInMemoryPgpKeys(signingKey.get(), signingPassphrase.get())
                sign(publishing.publications[PUBLICATION_NAME])
            }
        }
    }
}

tasks.register<Zip>("generateZip") {
    val publishTask = tasks.named(
        "publish${PUBLICATION_NAME.replaceFirstChar { it.uppercaseChar() }}PublicationToMavenLocalRepository",
        PublishToMavenRepository::class.java
    )
    from(publishTask.map { it.repository.url })
    archiveFileName.set("autobuilder-processor.zip")
}