
import com.yjy.convention.Plugins
import com.yjy.convention.applyPlugins
import com.yjy.convention.implementation
import com.yjy.convention.libs
import org.gradle.kotlin.dsl.dependencies

internal class AndroidRetrofitConventionPlugin : BuildLogicConventionPlugin({

    applyPlugins(Plugins.KOTLINX_SERIALIZATION)

    dependencies {
        implementation(libs.retrofit.core)
        implementation(libs.retrofit.kotlin.serialization)
        implementation(libs.okhttp.logging)
        implementation(libs.kotlinx.serialization.json)
    }
})
