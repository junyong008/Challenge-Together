@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.hilt)
}

android {
    namespace = "com.yjy.core.data"
}

dependencies {
    implementations(
        projects.core.common,
        projects.core.datastore,
        projects.core.network,
    )
}
