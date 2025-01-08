
import com.android.build.gradle.LibraryExtension
import com.yjy.convention.Plugins
import com.yjy.convention.applyPlugins
import com.yjy.convention.configureAndroidCompose
import org.gradle.kotlin.dsl.configure

internal class AndroidLibraryComposeConventionPlugin : BuildLogicConventionPlugin({

    applyPlugins(Plugins.ANDROID_LIBRARY, Plugins.COMPOSE_COMPILER)

    extensions.configure<LibraryExtension> {
        configureAndroidCompose(this)
    }
})
