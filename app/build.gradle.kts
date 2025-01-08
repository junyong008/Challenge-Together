@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

import java.util.Properties

plugins {
    alias(libs.plugins.custom.android.application)
    alias(libs.plugins.custom.android.application.compose)
    alias(libs.plugins.custom.android.application.firebase)
    alias(libs.plugins.custom.android.hilt)
    alias(libs.plugins.secrets)
    id("com.google.android.gms.oss-licenses-plugin")
}

android {
    namespace = "com.yjy.challengetogether"

    signingConfigs {
        create("release") {
            val propertiesFile = rootProject.file("keystore.properties")
            val properties = Properties()
            properties.load(propertiesFile.inputStream())

            storeFile = file(properties["STORE_FILE"] as String)
            storePassword = properties["STORE_PASSWORD"] as String
            keyAlias = properties["KEY_ALIAS"] as String
            keyPassword = properties["KEY_PASSWORD"] as String
        }
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            manifestPlaceholders["appName"] = "@string/app_name_debug"
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            manifestPlaceholders["appName"] = "@string/app_name"

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

secrets {
    defaultPropertiesFileName = "secrets.defaults.properties"
}

dependencies {
    ksp(libs.hilt.ext.compiler)

    implementations(
        projects.navigation.auth,
        projects.navigation.service,

        projects.model,

        projects.common.core,
        projects.common.designsystem,
        projects.common.navigation,

        projects.data.authApi,
        projects.data.userApi,
        projects.data.challengeApi,

        projects.platform.notifications,

        libs.android.installreferrer,
        libs.androidx.appcompat,
        libs.androidx.core.splashscreen,
        libs.androidx.startup,
        libs.androidx.navigation.compose,
        libs.androidx.hilt.navigation.compose,
        libs.androidx.work.ktx,
        libs.appsflyer.sdk,
        libs.google.ads,
        libs.hilt.ext.work,
        libs.timber,
    )

    runtimeOnly(projects.data.authImpl)
    runtimeOnly(projects.data.userImpl)
    runtimeOnly(projects.data.challengeImpl)
}
