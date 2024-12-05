@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.library.compose)
    alias(libs.plugins.custom.android.hilt)
}

android {
    namespace = "com.yjy.navigation.service"
}

dependencies {
    implementations(
        projects.common.core,
        projects.common.designsystem,
        projects.common.ui,
        projects.common.navigation,

        projects.data.authApi,
        projects.data.userApi,

        projects.model,

        projects.platform.network,
        projects.platform.time,

        projects.feature.home,
        projects.feature.together,
        projects.feature.completedchallenges,
        projects.feature.notification,
        projects.feature.addchallenge,
        projects.feature.waitingchallenge,
        projects.feature.startedchallenge,
        projects.feature.editchallenge,
        projects.feature.resetrecord,
        projects.feature.challengeboard,
        projects.feature.challengeranking,
        projects.feature.changepassword,

        libs.accompanist.permissions,
        libs.androidx.navigation.compose,
        libs.androidx.hilt.navigation.compose,
        libs.kotlinx.collections.immutable,
    )
    runtimeOnly(projects.data.authImpl)
    runtimeOnly(projects.data.userImpl)
}
