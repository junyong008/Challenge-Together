@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.library.compose)
}

android {
    namespace = "com.yjy.common.ui"
}

dependencies {
    implementations(
        projects.common.designsystem,
    )
}
