
import com.android.build.gradle.LibraryExtension
import com.yjy.convention.Plugins
import com.yjy.convention.applyPlugins
import com.yjy.convention.configureKotlinAndroid
import com.yjy.convention.implementation
import com.yjy.convention.libs
import com.yjy.convention.testImplementation
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

internal class AndroidLibraryConventionPlugin : BuildLogicConventionPlugin({

    applyPlugins(Plugins.ANDROID_LIBRARY, Plugins.KOTLIN_ANDROID)

    extensions.configure<LibraryExtension> {
        configureKotlinAndroid(this)

        defaultConfig.apply {
            targetSdk = libs.versions.targetSdk.get().toInt()
        }
    }

    dependencies {
        testImplementation(kotlin("test"))
        testImplementation(libs.mockk)
        implementation(libs.timber)
    }
})
