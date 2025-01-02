plugins {
    alias(libs.plugins.custom.android.library)
}

android {
    namespace = "com.yjy.model"

    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
}
