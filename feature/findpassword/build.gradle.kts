@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.feature)
}

android {
    namespace = "com.yjy.feature.findpassword"
}

dependencies {
    implementations(
        projects.common.network,

        projects.data.authApi,
    )
    runtimeOnly(projects.data.authImpl)
}
