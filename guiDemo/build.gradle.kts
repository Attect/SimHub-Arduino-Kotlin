import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("org.jetbrains.compose") version "1.2.2"
}

group = "studio.attect.simhub.arduino"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(project(":sdk"))
    implementation("com.fazecast:jSerialComm:2.9.3")// https://mvnrepository.com/artifact/com.fazecast/jSerialComm

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