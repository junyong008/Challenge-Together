@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.hilt)
    alias(libs.plugins.custom.android.room)
}

android {
    namespace = "com.yjy.core.database"
}

dependencies {
}
