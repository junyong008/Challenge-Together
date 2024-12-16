@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.library.compose)
    alias(libs.plugins.custom.android.hilt)
}

android {
    namespace = "com.yjy.platform.widget"
}

dependencies {
    implementations(
        projects.common.core,
        projects.common.network,
        projects.common.designsystem,
        projects.common.ui,

        projects.model,

        projects.domain,

        projects.data.challengeApi,

        libs.androidx.activity.compose,
        libs.androidx.glance.appwidget,
        libs.androidx.glance.material3,
        libs.androidx.hilt.navigation.compose,
    )

    runtimeOnly(projects.data.challengeImpl)
}
