
import com.android.build.gradle.LibraryExtension
import com.yjy.convention.androidTestImplementation
import com.yjy.convention.applyPlugins
import com.yjy.convention.implementation
import com.yjy.convention.libs
import com.yjy.convention.testImplementation
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal class AndroidFeatureConventionPlugin : BuildLogicConventionPlugin({

    applyPlugins(
        "custom.android.library",
        "custom.android.library.compose",
        "custom.android.hilt",
    )

    extensions.configure<LibraryExtension> {
        defaultConfig {
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    dependencies {
        implementation(project(":core:ui"))
        implementation(project(":core:designsystem"))
        implementation(project(":core:navigation"))

        implementation(libs.androidx.navigation.compose)
        implementation(libs.androidx.hilt.navigation.compose)
        testImplementation(libs.kotlinx.coroutines.test)
        androidTestImplementation(libs.bundles.androidx.compose.ui.test)
    }
})