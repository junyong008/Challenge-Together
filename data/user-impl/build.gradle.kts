@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.hilt)
}

android {
    namespace = "com.yjy.data.user_impl"
}

dependencies {
    implementations(
        projects.common.network,
        projects.model,

        projects.data.userApi,
        projects.data.networkApi,

        libs.kotlinx.coroutines.test,
    )

    runtimeOnly(projects.data.networkImpl)
}
