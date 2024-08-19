pluginManagement {
	repositories {
		maven("https://maven.quiltmc.org/repository/release")
		// Currently needed for Intermediary and other temporary dependencies
		maven("https://maven.fabricmc.net/")
		gradlePluginPortal()
	}
}
