@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.feature)
}

android {
    namespace = "com.yjy.feature.notificationsetting"
}

dependencies {
    implementations(
        projects.common.core,

        projects.model,

        projects.data.userApi,
    )

    runtimeOnly(projects.data.userImpl)
}
