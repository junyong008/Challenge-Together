@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.feature)
}

android {
    namespace = "com.yjy.feature.challengeboard"
}

dependencies {
    implementations(
        projects.common.core,
        projects.common.network,

        projects.model,

        projects.domain,

        projects.data.challengeApi,

        libs.androidx.paging.compose,
        libs.androidx.paging.ktx,
    )

    runtimeOnly(projects.data.challengeImpl)
}
