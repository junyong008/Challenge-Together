@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.feature)
}

android {
    namespace = "com.yjy.feature.themesetting"
}

dependencies {
    implementations(
        projects.common.core,

        projects.model,

        projects.data.userApi,

        libs.androidx.appcompat,
    )

    runtimeOnly(projects.data.userImpl)
}
