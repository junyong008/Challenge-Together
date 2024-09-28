plugins {
    alias(libs.plugins.custom.android.feature)
    alias(libs.plugins.custom.android.library.compose)
}

android {
    namespace = "com.yjy.feature.login"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.data)

    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
}