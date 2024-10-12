plugins {
    alias(libs.plugins.custom.android.library)
}

android {
    namespace = "com.yjy.data.datastore_api"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}
