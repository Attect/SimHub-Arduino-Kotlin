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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.3")
    implementation("com.fazecast:jSerialComm:2.9.2")// https://mvnrepository.com/artifact/com.fazecast/jSerialComm

}


tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

