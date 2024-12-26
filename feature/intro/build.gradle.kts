@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.feature)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.yjy.feature.intro"

    buildFeatures {
        buildConfig = true
    }
}

secrets {
    defaultPropertiesFileName = "secrets.defaults.properties"
}

dependencies {
    implementations(
        projects.common.network,

        projects.data.authApi,

        libs.androidx.credentials,
        libs.androidx.credentials.auth,
        libs.google.identity,
        libs.kakao.sdk.login,
        libs.naver.oauth,
    )

    runtimeOnly(projects.data.authImpl)
}
