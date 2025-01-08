@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.library.compose)
    alias(libs.plugins.custom.android.hilt)
}

android {
    namespace = "com.yjy.navigation.auth"
}

dependencies {
    implementations(
        projects.common.core,
        projects.common.designsystem,
        projects.common.ui,
        projects.common.navigation,

        projects.data.userApi,

        projects.model,

        projects.platform.time,

        projects.feature.intro,
        projects.feature.login,
        projects.feature.signup,
        projects.feature.findpassword,
        projects.feature.changepassword,

        libs.androidx.navigation.compose,
        libs.androidx.hilt.navigation.compose,
    )

    runtimeOnly(projects.data.userImpl)
}
