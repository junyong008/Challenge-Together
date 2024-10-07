@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.retrofit)
    alias(libs.plugins.custom.android.hilt)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.yjy.core.network"

    buildFeatures {
        buildConfig = true
    }
}

secrets {
    defaultPropertiesFileName = "secrets.defaults.properties"
}

dependencies {
    implementations(
        projects.core.common,
    )
}
