@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.feature)
}

android {
    namespace = "com.yjy.feature.waitingchallenge"
}

dependencies {
    implementations(
        projects.common.core,
        projects.common.network,

        projects.model,

        projects.data.challengeApi,
    )

    runtimeOnly(projects.data.challengeImpl)
}