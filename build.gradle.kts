plugins {
	id("maven-publish")
	alias(libs.plugins.quilt.loom)
}

class ModData {
	val version = property("version").toString()
	val mavenGroup = property("maven_group").toString()
	val id = property("id").toString()
}

val mod = ModData()

base {
	archivesName = mod.id
}

version = "${mod.version}+${libs.versions.minecraft.get()}"
group = mod.mavenGroup

repositories {
	// Add repositories to retrieve artifacts from in here.
	// You should only use this when depending on other mods because
	// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
	// See https://docs.gradle.org/current/userguide/declaring_repositories.html
	// for more information about repositories.

	maven("https://api.modrinth.com/maven") { name = "Modrinth" }
	maven("https://maven.nucleoid.xyz/") { name = "Nucleoid" }
}

loom {
	// Loom and Loader both use this block in order to gather more information about your mod.
	mods {
		// This should match your mod id.
		create(mod.id) {
			// Tell Loom about each source set used by your mod here. This ensures that your mod's classes are properly transformed by Loader.
			sourceSet("main")
			// If you shade (directly include classes, not JiJ) a dependency into your mod, include it here using one of these methods:
			// dependency("com.example.shadowedmod:1.2.3")
			// configuration("exampleShadedConfigurationName")
		}
	}
}

// All the dependencies are declared at gradle/libs.version.toml and referenced with "libs.<id>"
// See https://docs.gradle.org/current/userguide/platforms.html for information on how version catalogs work.
dependencies {
	minecraft(libs.minecraft)
	mappings(variantOf(libs.quilt.mappings) { classifier("intermediary-v2") })
	modImplementation(libs.quilt.loader)

	// QSL is not a complete API; You will need Quilted Fabric API to fill in the gaps.
	// Quilted Fabric API will automatically pull in the correct QSL version.
	modImplementation(libs.bundles.quilted.fabric.api)
	// modImplementation(libs.bundles.quilted.fabric.api.deprecated) // If you wish to use Fabric API's deprecated modules, you can replace the above line with this one

	modRuntimeOnly(libs.mod.menu)
}

tasks.processResources {
	val map = mapOf("version" to version, "id" to mod.id, "group" to project.group)

	inputs.properties(map)

	filesMatching("quilt.mod.json") {
		expand(map)
	}
}

java {
	// Still required by IDEs such as Eclipse and Visual Studio Code
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21

	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()

	// If this mod is going to be a library, then it should also generate Javadocs in order to aid with development.
	// Uncomment this line to generate them.
	// withJavadocJar()
}

// If you plan to use a different file for the license, don't forget to change the file name here!
tasks.jar {
	from(project.file("LICENSE"))
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
