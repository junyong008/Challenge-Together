@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.yjy.data.network_api"

    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
}

dependencies {
    implementations(
        projects.common.network,

        libs.kotlinx.coroutines.core,
        libs.kotlinx.serialization.json,
    )
}
