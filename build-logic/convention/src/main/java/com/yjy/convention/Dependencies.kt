package com.yjy.convention

import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.implementation(dependencyNotation: Any): Dependency? {
    return add("implementation", dependencyNotation)
}

fun DependencyHandler.testImplementation(dependencyNotation: Any): Dependency? {
    return add("testImplementation", dependencyNotation)
}

fun DependencyHandler.androidTestImplementation(dependencyNotation: Any): Dependency? {
    return add("androidTestImplementation", dependencyNotation)
}

fun DependencyHandler.debugImplementation(dependencyNotation: Any): Dependency? {
    return add("debugImplementation", dependencyNotation)
}

fun DependencyHandler.ksp(dependencyNotation: Any): Dependency? {
    return add("ksp", dependencyNotation)
}

fun DependencyHandler.coreLibraryDesugaring(dependencyNotation: Any): Dependency? {
    return add("coreLibraryDesugaring", dependencyNotation)
}

fun DependencyHandler.detektPlugins(dependencyNotation: Any): Dependency? {
    return add("detektPlugins", dependencyNotation)
}