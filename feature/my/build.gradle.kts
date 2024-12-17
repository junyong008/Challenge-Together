@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.feature)
}

android {
    namespace = "com.yjy.feature.my"
}

dependencies {
    implementations(
        projects.common.network,

        projects.model,

        projects.domain,

        projects.data.challengeApi,
        projects.data.userApi,

        libs.google.oss.licenses,
    )

    runtimeOnly(projects.data.challengeImpl)
    runtimeOnly(projects.data.userImpl)
}
