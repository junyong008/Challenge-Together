@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.hilt)
}

android {
    namespace = "com.yjy.platform.worker"
}

dependencies {
    ksp(libs.hilt.ext.compiler)

    implementations(
        projects.common.core,
        projects.common.network,

        projects.model,

        projects.domain,

        projects.data.userApi,
        projects.data.challengeApi,

        libs.androidx.work.ktx,
        libs.hilt.ext.work,
        libs.kotlinx.coroutines.guava,
    )

    runtimeOnly(projects.data.userImpl)
    runtimeOnly(projects.data.challengeImpl)
    androidTestImplementation(libs.androidx.work.testing)
}
