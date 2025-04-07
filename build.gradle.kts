plugins {
    id("maven-publish")
    id("fabric-loom") version "1.10-SNAPSHOT"
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}

class ModData {
    val version = property("version").toString()
    val mavenGroup = property("maven_group").toString()
    val id = property("id").toString()

    val deps = ModDeps()
}

class ModDeps {
    val fabricLoader = "net.fabricmc:fabric-loader:${property("deps.fabric_loader")}"

    val fabricApi = "net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}"
    val modmenu = "maven.modrinth:modmenu:${property("deps.modmenu")}"
    val polymerCore = "eu.pb4:polymer-core:${property("deps.polymer_core")}"
    val placeholderApi = "eu.pb4:placeholder-api:${property("deps.placeholder_api")}"
}

val mod = ModData()
val publishFor = property("publish.for").toString().split(" ").filter { it.isNotEmpty() }

base {
    archivesName = mod.id
}

group = mod.mavenGroup

if (stonecutter.current.isActive) {
    rootProject.tasks.register("client") {
        group = "project"
        dependsOn(tasks.named("runClient"))
    }
}

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.

    maven("https://maven.quiltmc.org/repository/release") { name = "Quilt" }
    maven("https://api.modrinth.com/maven") { name = "Modrinth" }
    maven("https://maven.nucleoid.xyz/") { name = "Nucleoid" }
}

loom {
    // Loom and Loader both use this block in order to gather more information about your mod.
    mods {
        create(mod.id) {
            // Tell Loom about each source set used by your mod here. This ensures that your mod's classes are properly transformed by Loader.
            sourceSet("main")
            // If you shade (directly include classes, not JiJ) a dependency into your mod, include it here using one of these methods:
            // dependency("com.example.shadowedmod:1.2.3")
            // configuration("exampleShadedConfigurationName")
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${stonecutter.current.project}")
    mappings(loom.officialMojangMappings())
    modImplementation(mod.deps.fabricLoader)

    modImplementation(mod.deps.placeholderApi)
    modImplementation(mod.deps.polymerCore)

    modRuntimeOnly(mod.deps.fabricApi)
    modRuntimeOnly(mod.deps.modmenu)
}

tasks.processResources {
    val map = mapOf("version" to version, "id" to mod.id, "group" to project.group)

    inputs.properties(map)

    filesMatching(listOf("quilt.mod.json", "fabric.mod.json")) {
        expand(map)
    }
}

java {
    // Still required by IDEs such as Eclipse and Visual Studio Code
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task if it is present.
    // If you remove this line, sources will not be generated.
    // withSourcesJar()

    // If this mod is going to be a library, then it should also generate Javadocs in order to aid with development.
    // Uncomment this line to generate them.
    // withJavadocJar()
}

// If you plan to use a different file for the license, don't forget to change the file name here!
tasks.jar {
    from(project.file("LICENSE"))
}

publishMods {
    displayName = "styled renaming ${mod.version}"
    file = tasks.remapJar.get().archiveFile
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = STABLE

    modLoaders.addAll("fabric", "quilt")

    dryRun = !providers.environmentVariable("MODRINTH_TOKEN").isPresent
            || publishFor.isEmpty()

    modrinth {
        projectId = "Z87eUIv0"
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.addAll(publishFor)

        projectDescription = rootProject.file("README.md").readText()

        requires("polymer", "placeholder-api")
    }
}

// Configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}
