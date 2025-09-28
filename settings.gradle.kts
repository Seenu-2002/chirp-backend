pluginManagement {
    includeBuild("build-logic")
	repositories {
		maven { url = uri("https://repo.spring.io/snapshot") }
		gradlePluginPortal()
	}
}
rootProject.name = "chirp"
include("app")
include("chat")
include("user")
include("notification")
include("common")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")