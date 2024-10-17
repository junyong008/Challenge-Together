@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.feature)
}

android {
    namespace = "com.yjy.feature.addchallenge"
}

dependencies {
    implementations(
        projects.model,
    )
}
