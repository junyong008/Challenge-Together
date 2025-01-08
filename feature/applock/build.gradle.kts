@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.feature)
}

android {
    namespace = "com.yjy.feature.applock"
}

dependencies {
    implementations(
        projects.common.core,

        projects.model,

        projects.platform.widget,

        projects.data.authApi,
        projects.data.userApi,

        libs.androidx.biometric,
    )

    runtimeOnly(projects.data.authImpl)
    runtimeOnly(projects.data.userImpl)
}
