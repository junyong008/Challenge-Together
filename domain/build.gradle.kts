@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
}

android {
    namespace = "com.yjy.domain"
}

dependencies {
    implementations(
        projects.model,

        projects.data.challengeApi,
        projects.data.userApi,

        libs.javax.inject,
        libs.kotlinx.coroutines.core,
    )

    runtimeOnly(projects.data.challengeImpl)
    runtimeOnly(projects.data.userImpl)
}
