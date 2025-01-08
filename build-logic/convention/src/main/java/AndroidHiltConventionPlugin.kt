
import com.yjy.convention.Plugins
import com.yjy.convention.applyPlugins
import com.yjy.convention.implementation
import com.yjy.convention.ksp
import com.yjy.convention.libs
import org.gradle.kotlin.dsl.dependencies

internal class AndroidHiltConventionPlugin : BuildLogicConventionPlugin({
    applyPlugins(Plugins.HILT, Plugins.KSP)

    dependencies {
        implementation(libs.hilt.android)
        ksp(libs.hilt.compiler)
    }
})