import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.5.10"
}

group = "studio.attect.simhub.arduino.sdk"
version = "1.0"

repositories {
    jcenter()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}


dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    implementation("com.fazecast:jSerialComm:2.7.0")

}


tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

