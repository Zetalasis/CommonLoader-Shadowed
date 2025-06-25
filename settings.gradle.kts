rootProject.name = "CommonLoader-Shadowed"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") // ← Fabric Maven for Loom
        maven("https://repo.spongepowered.org/repository/maven-public/")
    }
}