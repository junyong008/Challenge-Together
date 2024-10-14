plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.custom.android.library.compose)
}

android {
    namespace = "com.yjy.common.designsystem"
}

dependencies {
    implementation(projects.model)
    implementation(libs.lottie.compose)
}
