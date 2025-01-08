@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.feature)
}

android {
    namespace = "com.yjy.feature.premium"
}

dependencies {
    implementations(
        projects.common.network,

        projects.data.userApi,

        libs.billing.ktx,
    )

    runtimeOnly(projects.data.userImpl)
}
