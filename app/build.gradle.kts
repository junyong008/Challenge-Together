@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

plugins {
    alias(libs.plugins.custom.android.application)
    alias(libs.plugins.custom.android.application.compose)
    alias(libs.plugins.custom.android.hilt)
}

android {
    namespace = "com.yjy.challengetogether"

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            manifestPlaceholders += mapOf(
                "appName" to "@string/app_name_debug",
            )
        }
        release {
            isDebuggable = false
            manifestPlaceholders += mapOf(
                "appName" to "@string/app_name",
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
    implementations(
        projects.navigation.auth,
        projects.navigation.service,

        projects.common.core,
        projects.common.designsystem,
        projects.common.navigation,

        projects.data.authApi,

        libs.androidx.core.splashscreen,
        libs.androidx.startup,
        libs.androidx.navigation.compose,
        libs.androidx.hilt.navigation.compose,
        libs.timber,
    )
    runtimeOnly(projects.data.authImpl)
}
