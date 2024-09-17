plugins {
    alias(libs.plugins.custom.android.application)
    alias(libs.plugins.custom.android.application.compose)
    alias(libs.plugins.custom.android.hilt)
}

android {
    namespace = "com.yjy.challengetogether"

    defaultConfig {
        applicationId = "com.yjy.challengetogether_demo"
        versionCode = 8
        versionName = "2.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.feature.login)

    implementation(projects.core.designsystem)

    implementation(libs.androidx.core.splashscreen)
}