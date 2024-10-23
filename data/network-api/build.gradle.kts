plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.yjy.data.network_api"
}

dependencies {
    implementation(projects.common.network)
    implementation(libs.kotlinx.serialization.json)
}
