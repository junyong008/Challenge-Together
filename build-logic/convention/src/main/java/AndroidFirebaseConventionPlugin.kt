
import com.yjy.convention.implementation
import com.yjy.convention.libs
import org.gradle.kotlin.dsl.dependencies

internal class AndroidFirebaseConventionPlugin : BuildLogicConventionPlugin({
    dependencies {
        implementation(platform(libs.firebase.bom))
        implementation(libs.firebase.database)
        implementation(libs.firebase.crashlytics)
    }
})
