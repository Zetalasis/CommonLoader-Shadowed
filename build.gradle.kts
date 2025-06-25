plugins {
    id("java")
    id("fabric-loom") version "1.6-SNAPSHOT"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.zetalasis.commonloader"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    // maven("https://repo.spongepowered.org/repository/maven-public/")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.google.code.gson:gson:2.10.1")

    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.22.1")
    implementation("org.apache.logging.log4j:log4j-core:2.22.1")

    minecraft("com.mojang:minecraft:1.20.4")
    mappings("net.fabricmc:yarn:1.20.4+build.1:v2")

    implementation("org.ow2.asm:asm:9.6")
    implementation("org.ow2.asm:asm-commons:9.6")

    // implementation("org.spongepowered:mixin:0.8.5")
}

loom {
    officialMojangMappings()
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        from("src/main/resources/META-INF/MANIFEST.MF")
    }
}

tasks.withType<Jar> {
    manifest {
        attributes(
            "MixinConfigs" to "mixins.commonloader.json"
        )
    }
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        mergeServiceFiles()
        manifest {
            attributes(
                "Premain-Class" to "com.zetalasis.commonloader.Main",
                "Can-Redefine-Classes" to "true",
                "MixinConfigs" to "mixins.commonloader.json"
            )
        }
        relocate("org.objectweb.asm", "com.zetalasis.shadow.asm")
    }

    build {
        dependsOn(shadowJar)
    }
}