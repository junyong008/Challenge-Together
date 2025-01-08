@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.feature)
}

android {
    namespace = "com.yjy.feature.linkaccount"
}

dependencies {
    implementations(
        projects.common.network,

        projects.data.userApi,
    )

    runtimeOnly(projects.data.userImpl)
}
