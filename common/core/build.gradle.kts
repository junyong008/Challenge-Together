@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.library.compose)
    alias(libs.plugins.custom.android.hilt)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.yjy.common.core"

    buildFeatures {
        buildConfig = true
    }
}

secrets {
    defaultPropertiesFileName = "secrets.defaults.properties"
}

dependencies {
    implementations(
        libs.androidx.credentials,
        libs.androidx.credentials.auth,
        libs.androidx.hilt.navigation.compose,
        libs.google.identity,
        libs.kakao.sdk.login,
        libs.naver.oauth,
    )

    testImplementation(libs.kotlinx.coroutines.test)
}
