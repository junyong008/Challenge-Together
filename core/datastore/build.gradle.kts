@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.yjy.core.datastore"
}

dependencies {
    implementations(
        libs.androidx.datastore.preferences,
        libs.kotlinx.serialization.json,
    )
}
