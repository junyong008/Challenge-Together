enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ChallengeTogether"

include(
    ":app",

    ":core:common",
    ":core:data",
    ":core:database",
    ":core:datastore",
    ":core:designsystem",
    ":core:domain",
    ":core:model",
    ":core:navigation",
    ":core:network",
    ":core:notifications",
    ":core:ui",

    ":feature:login",
    ":feature:signup",
)
