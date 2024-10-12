plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.yjy.common.navigation"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
