@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.yjy.core.navigation"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
