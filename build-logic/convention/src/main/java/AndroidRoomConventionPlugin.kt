import androidx.room.gradle.RoomExtension
import com.google.devtools.ksp.gradle.KspExtension
import com.yjy.convention.Plugins
import com.yjy.convention.applyPlugins
import com.yjy.convention.implementation
import com.yjy.convention.ksp
import com.yjy.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal class AndroidRoomConventionPlugin : BuildLogicConventionPlugin({

    applyPlugins(Plugins.ROOM, Plugins.KOTLINX_SERIALIZATION, Plugins.KSP)

    extensions.configure<KspExtension> {
        arg("room.generateKotlin", "true")
    }

    extensions.configure<RoomExtension> {
        schemaDirectory("$projectDir/schemas")
    }

    dependencies {
        implementation(libs.room.runtime)
        implementation(libs.room.ktx)
        implementation(libs.room.paging)
        implementation(libs.kotlinx.serialization.json)
        ksp(libs.room.compiler)
    }
})
