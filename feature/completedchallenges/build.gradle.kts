@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.feature)
}

android {
    namespace = "com.yjy.feature.completedchallenges"
}

dependencies {
    implementations(
        projects.common.core,

        projects.model,

        projects.domain,

        projects.data.challengeApi,
    )

    runtimeOnly(projects.data.challengeImpl)
}
