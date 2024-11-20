plugins {
    alias(libs.plugins.custom.android.library)
}

android {
    namespace = "com.yjy.data.database_api"
}

dependencies {
    implementation(libs.room.ktx)
    implementation(libs.androidx.paging.ktx)
}
