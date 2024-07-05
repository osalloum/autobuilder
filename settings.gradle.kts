plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "AutoBuilder"
include("AutoBuilder.Processor")
include("Annotations")
include("AutoBuilder.Processor")
include("AutoBuilder.Annotations")
include("AutoBuilder.Test")
