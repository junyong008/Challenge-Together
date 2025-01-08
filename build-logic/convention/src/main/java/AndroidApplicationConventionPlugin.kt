
import com.android.build.api.dsl.ApplicationExtension
import com.yjy.convention.Plugins
import com.yjy.convention.applyPlugins
import com.yjy.convention.configureKotlinAndroid
import com.yjy.convention.libs
import org.gradle.kotlin.dsl.configure


internal class AndroidApplicationConventionPlugin : BuildLogicConventionPlugin({

    applyPlugins(Plugins.ANDROID_APPLICATION, Plugins.KOTLIN_ANDROID)

    extensions.configure<ApplicationExtension> {
        configureKotlinAndroid(this)

        defaultConfig {
            applicationId = libs.versions.packageName.get()
            targetSdk = libs.versions.targetSdk.get().toInt()
            versionCode = libs.versions.versionCode.get().toInt()
            versionName = libs.versions.versionName.get()
        }
    }
})