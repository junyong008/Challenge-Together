@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
}

android {
    namespace = "com.yjy.domain"
}

dependencies {
    implementations(
        projects.common.core,
        projects.common.network,

        projects.model,

        projects.data.authApi,
        projects.data.challengeApi,
        projects.data.userApi,

        libs.androidx.paging.ktx,
        libs.javax.inject,
        libs.kotlinx.coroutines.core,
    )

    testImplementation(libs.kotlinx.coroutines.test)

    runtimeOnly(projects.data.authImpl)
    runtimeOnly(projects.data.challengeImpl)
    runtimeOnly(projects.data.userImpl)
}
