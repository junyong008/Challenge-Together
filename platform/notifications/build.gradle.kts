plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.hilt)
}

android {
    namespace = "com.yjy.platform.notifications"
}

dependencies {
    implementation(projects.common.core)
}
