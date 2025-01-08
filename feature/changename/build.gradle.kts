@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.feature)
}

android {
    namespace = "com.yjy.feature.changename"
}

dependencies {
    implementations(
        projects.common.network,

        projects.data.userApi,
    )
    runtimeOnly(projects.data.userImpl)
}
