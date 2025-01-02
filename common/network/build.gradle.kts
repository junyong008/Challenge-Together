plugins {
    alias(libs.plugins.custom.android.library)
}

android {
    namespace = "com.yjy.common.network"

    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
}
