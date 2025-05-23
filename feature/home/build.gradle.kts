@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.feature)
    alias(libs.plugins.custom.android.firebase)
}

android {
    namespace = "com.yjy.feature.home"
}

dependencies {
    implementations(
        projects.common.network,

        projects.model,

        projects.domain,

        projects.platform.widget,

        projects.data.challengeApi,
        projects.data.userApi,

        libs.google.play.review,
        libs.google.play.review.ktx,
    )

    runtimeOnly(projects.data.challengeImpl)
    runtimeOnly(projects.data.userImpl)
}
