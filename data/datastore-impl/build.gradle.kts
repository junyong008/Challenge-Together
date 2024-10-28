@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.yjy.data.datastore_impl"
}

dependencies {
    implementations(
        projects.data.datastoreApi,

        libs.androidx.datastore.preferences,
        libs.kotlinx.serialization.json,
    )
}
