@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.feature)
}

android {
    namespace = "com.yjy.feature.together"
}

dependencies {
    implementations(
        projects.common.core,

        projects.model,

        projects.data.challengeApi,

        libs.androidx.paging.compose,
        libs.androidx.paging.ktx,
    )

    runtimeOnly(projects.data.challengeImpl)
}
