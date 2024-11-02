@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.hilt)
}

android {
    namespace = "com.yjy.data.auth_impl"
}

dependencies {
    implementations(
        projects.common.network,

        projects.data.authApi,
        projects.data.datastoreApi,
        projects.data.networkApi,
    )

    testImplementation(libs.kotlinx.coroutines.test)

    runtimeOnly(projects.data.datastoreImpl)
    runtimeOnly(projects.data.networkImpl)
}
