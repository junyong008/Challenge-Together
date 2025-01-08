@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.feature)
}

android {
    namespace = "com.yjy.feature.community"
}

dependencies {
    implementations(
        projects.common.network,

        projects.model,

        projects.domain,

        projects.data.communityApi,
        projects.data.userApi,

        libs.androidx.paging.compose,
        libs.androidx.paging.ktx,
    )

    runtimeOnly(projects.data.communityImpl)
    runtimeOnly(projects.data.userImpl)
}
