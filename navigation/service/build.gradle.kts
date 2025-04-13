@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.library.compose)
    alias(libs.plugins.custom.android.hilt)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.yjy.navigation.service"

    buildFeatures {
        buildConfig = true
    }
}

secrets {
    defaultPropertiesFileName = "secrets.defaults.properties"
}

dependencies {
    implementations(
        projects.common.core,
        projects.common.designsystem,
        projects.common.ui,
        projects.common.navigation,
        projects.common.network,

        projects.data.authApi,
        projects.data.userApi,
        projects.data.challengeApi,

        projects.domain,

        projects.model,

        projects.platform.widget,
        projects.platform.worker,
        projects.platform.network,
        projects.platform.time,

        projects.feature.home,
        projects.feature.together,
        projects.feature.community,
        projects.feature.my,
        projects.feature.applock,
        projects.feature.completedchallenges,
        projects.feature.notification,
        projects.feature.notificationsetting,
        projects.feature.premium,
        projects.feature.changename,
        projects.feature.linkaccount,
        projects.feature.deleteaccount,
        projects.feature.addchallenge,
        projects.feature.waitingchallenge,
        projects.feature.startedchallenge,
        projects.feature.editchallenge,
        projects.feature.resetrecord,
        projects.feature.challengeboard,
        projects.feature.challengeranking,
        projects.feature.changepassword,

        libs.accompanist.permissions,
        libs.androidx.appcompat,
        libs.androidx.navigation.compose,
        libs.androidx.hilt.navigation.compose,
        libs.appsflyer.sdk,
        libs.google.ads,
        libs.kotlinx.collections.immutable,
    )

    runtimeOnly(projects.data.authImpl)
    runtimeOnly(projects.data.userImpl)
    runtimeOnly(projects.data.challengeImpl)
}
