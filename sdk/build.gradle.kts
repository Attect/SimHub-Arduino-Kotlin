import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.8.0"
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

    testImplementation(kotlin("test"))

    // https://mvnrepository.com/artifact/com.github.oshi/oshi-core
    testImplementation("com.github.oshi:oshi-core:6.4.0")

//    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

