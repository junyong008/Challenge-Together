plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.yjy.common.navigation"
}

dependencies {
    implementation(projects.model)
    implementation(libs.kotlinx.serialization.json)
}
