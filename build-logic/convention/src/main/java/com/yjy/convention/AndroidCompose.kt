package com.yjy.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        dependencies {
            implementation(libs.bundles.androidx.compose)
            debugImplementation(libs.bundles.androidx.compose.debug)
        }

        configure<ComposeCompilerGradlePluginExtension> {
            metricsDestination.file("build/composeMetrics")
            reportsDestination.file("build/composeReports")

            includeSourceInformation = true
            stabilityConfigurationFile = project.rootDir.resolve("compose_compiler_config.conf")
        }
    }
}
