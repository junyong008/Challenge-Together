plugins {
    `kotlin-dsl`
    kotlin("jvm") version libs.versions.kotlin.get()
    alias(libs.plugins.gradle.dependency.handler.extensions)
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)

    compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    val conventionPluginClasses = listOf(
        "android.application" to "AndroidApplicationConventionPlugin",
        "android.application.compose" to "AndroidApplicationComposeConventionPlugin",
        "android.application.firebase" to "AndroidApplicationFirebaseConventionPlugin",
        "android.firebase" to "AndroidFirebaseConventionPlugin",
        "android.library" to "AndroidLibraryConventionPlugin",
        "android.library.compose" to "AndroidLibraryComposeConventionPlugin",
        "android.feature" to "AndroidFeatureConventionPlugin",
        "android.hilt" to "AndroidHiltConventionPlugin",
        "android.retrofit" to "AndroidRetrofitConventionPlugin",
        "android.room" to "AndroidRoomConventionPlugin",
    )

    plugins {
        conventionPluginClasses.forEach { pluginClass ->
            pluginRegister(pluginClass)
        }
    }
}

fun NamedDomainObjectContainer<PluginDeclaration>.pluginRegister(data: Pair<String, String>) {
    val (pluginName, className) = data
    register(pluginName) {
        id = "custom.$pluginName"
        implementationClass = className
    }
}