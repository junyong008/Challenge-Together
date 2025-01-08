plugins {
    alias(libs.plugins.custom.android.library)
}

android {
    namespace = "com.yjy.data.auth_api"
}

dependencies {
    implementation(projects.common.network)
    implementation(libs.kotlinx.coroutines.core)
}
