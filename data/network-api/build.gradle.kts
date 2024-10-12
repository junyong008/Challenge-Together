plugins {
    alias(libs.plugins.custom.android.library)
}

android {
    namespace = "com.yjy.data.network_api"
}

dependencies {
    implementation(projects.common.network)
}
