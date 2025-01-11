plugins {
    kotlin("jvm")
    id("maven-publish")
    signing
}

val GROUP_ID: String = project.properties["group.id"].toString()
val VERSION: String = project.properties["version"].toString()
val ARTIFACT_ID = "AutoBuilder.Annotations"
val PUBLICATION_NAME = "autoBuilderAnnotations"

group = GROUP_ID
version = VERSION

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
                name = "AutoBuilder.Annotations"
                description = "AutoBuilder: A Kotlin Symbol Processing (KSP) library for automatic builder class generation. Supports default values for both primitive and non-primitive properties."
                url = "https://github.com/osalloum/autobuilder"
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
                        id = "osalloum"
                        name = "Omar Salloum"
                        email = "eyelet.taros28@icloud.com"
                    }
                }
                scm {
                    connection = "scm:git:git@github.com:osalloum/autobuilder.git"
                    developerConnection = "scm:git:git@github.com:osalloum/autobuilder.git"
                    url = "https://github.com/osalloum/autobuilder"
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
    archiveFileName.set("autobuilder-annotations_${VERSION}.zip")
}