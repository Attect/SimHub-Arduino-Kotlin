pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    
}
rootProject.name = "SimHub-Arduino-Kotlin"
include("sdk")
include("guiDemo")

