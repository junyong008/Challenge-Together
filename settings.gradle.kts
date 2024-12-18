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

    ":common:core",
    ":common:designsystem",
    ":common:navigation",
    ":common:network",
    ":common:ui",

    ":data:auth-api",
    ":data:auth-impl",
    ":data:user-api",
    ":data:user-impl",
    ":data:challenge-api",
    ":data:challenge-impl",
    ":data:community-api",
    ":data:community-impl",
    ":data:database-api",
    ":data:database-impl",
    ":data:datastore-api",
    ":data:datastore-impl",
    ":data:network-api",
    ":data:network-impl",

    ":platform:network",
    ":platform:notifications",
    ":platform:time",
    ":platform:widget",
    ":platform:worker",

    ":domain",

    ":model",

    ":navigation:auth",
    ":navigation:service",

    ":feature:home",
    ":feature:together",
    ":feature:community",
    ":feature:my",
    ":feature:applock",
    ":feature:notification",
    ":feature:notificationsetting",
    ":feature:addchallenge",
    ":feature:editchallenge",
    ":feature:completedchallenges",
    ":feature:waitingchallenge",
    ":feature:startedchallenge",
    ":feature:resetrecord",
    ":feature:challengeboard",
    ":feature:challengeranking",

    ":feature:login",
    ":feature:signup",
    ":feature:findpassword",
    ":feature:changepassword",
)
