plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.library.compose)
    alias(libs.plugins.custom.android.hilt)
}

android {
    namespace = "com.yjy.common.core"
}

dependencies {
    implementation(libs.androidx.hilt.navigation.compose)
}
