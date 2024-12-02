@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.feature)
}

android {
    namespace = "com.yjy.feature.notification"
}

dependencies {
    implementations(
        projects.common.core,
        projects.common.network,

        projects.model,

        projects.data.userApi,

        libs.androidx.paging.compose,
        libs.androidx.paging.ktx,
    )

    runtimeOnly(projects.data.userImpl)
}
