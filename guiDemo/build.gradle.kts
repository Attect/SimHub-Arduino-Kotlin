import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev713"
}

group = "studio.attect.simhub.arduino"
version = "1.0"

repositories {
    google()
    jcenter()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(project(":sdk"))
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}


compose.desktop {
    application {
        mainClass = "studio.attect.simhub.arduino.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "SimHub-Arduino-Kotlin"
            packageVersion = "1.0.0"
        }
    }
}