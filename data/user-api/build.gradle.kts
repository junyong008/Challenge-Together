@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
}

android {
    namespace = "com.yjy.data.user_api"
}

dependencies {
    implementations(
        projects.common.network,
        projects.model,

        libs.androidx.paging.ktx,
        libs.kotlinx.coroutines.core,
    )
}
