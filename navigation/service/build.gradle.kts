@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.library.compose)
    alias(libs.plugins.custom.android.hilt)
}

android {
    namespace = "com.yjy.navigation.service"
}

dependencies {
    implementations(
        projects.common.core,
        projects.common.designsystem,
        projects.common.navigation,

        projects.data.authApi,

        projects.feature.home,
        projects.feature.changepassword,

        libs.androidx.navigation.compose,
        libs.androidx.hilt.navigation.compose,
        libs.kotlinx.collections.immutable,
    )
    runtimeOnly(projects.data.authImpl)
}