
import com.android.build.api.dsl.ApplicationExtension
import com.yjy.convention.Plugins
import com.yjy.convention.applyPlugins
import com.yjy.convention.configureAndroidCompose
import org.gradle.kotlin.dsl.configure

internal class AndroidApplicationComposeConventionPlugin : BuildLogicConventionPlugin({

    applyPlugins(Plugins.ANDROID_APPLICATION, Plugins.COMPOSE_COMPILER)

    extensions.configure<ApplicationExtension> {
        configureAndroidCompose(this)
    }
})