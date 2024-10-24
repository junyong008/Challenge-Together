@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.hilt)
}

android {
    namespace = "com.yjy.platform.network"
}

dependencies {
    implementations(
        projects.common.core,

        libs.kotlinx.coroutines.core,
    )
}
