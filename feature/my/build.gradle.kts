@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.feature)
}

android {
    namespace = "com.yjy.feature.my"
}

dependencies {
    implementations(
        projects.common.network,

        projects.model,

        projects.domain,

        projects.data.challengeApi,
        projects.data.userApi,

        libs.androidx.credentials,
        libs.androidx.credentials.auth,
        libs.google.identity,
        libs.google.oss.licenses,
        libs.kakao.sdk.login,
        libs.naver.oauth,
    )

    runtimeOnly(projects.data.challengeImpl)
    runtimeOnly(projects.data.userImpl)
}
