import com.yjy.convention.Plugins
import com.yjy.convention.applyPlugins
import com.yjy.convention.implementation
import com.yjy.convention.libs
import org.gradle.kotlin.dsl.dependencies

internal class AndroidApplicationFirebaseConventionPlugin : BuildLogicConventionPlugin({
    applyPlugins(Plugins.GOOGLE_SERVICES, Plugins.FIREBASE_CRASHLYTICS)

    dependencies {
        implementation(platform(libs.firebase.bom))
        implementation(libs.firebase.analytics)
        implementation(libs.firebase.crashlytics)
        implementation(libs.firebase.messaging)
    }
})
