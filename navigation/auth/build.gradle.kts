@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.library.compose)
    alias(libs.plugins.custom.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.yjy.navigation.auth"
}

dependencies {
    implementations(
        projects.common.designsystem,

        projects.feature.login,
        projects.feature.signup,
        projects.feature.findpassword,
        projects.feature.changepassword,

        libs.androidx.navigation.compose,
        libs.kotlinx.serialization.json,
    )
}
