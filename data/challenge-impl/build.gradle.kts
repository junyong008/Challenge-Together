@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.hilt)
}

android {
    namespace = "com.yjy.data.challenge_impl"
}

dependencies {
    implementations(
        projects.common.core,
        projects.common.network,

        projects.model,

        projects.data.challengeApi,
        projects.data.databaseApi,
        projects.data.datastoreApi,
        projects.data.networkApi,
    )

    testImplementation(libs.kotlinx.coroutines.test)

    runtimeOnly(projects.data.databaseImpl)
    runtimeOnly(projects.data.datastoreImpl)
    runtimeOnly(projects.data.networkImpl)
}
