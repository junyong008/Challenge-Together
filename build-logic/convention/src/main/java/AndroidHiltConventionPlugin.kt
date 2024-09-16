import com.yjy.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.devtools.ksp")
            apply(plugin = "dagger.hilt.android.plugin")

            dependencies {
                add("ksp", libs.findLibrary("hilt.compiler").get())
                add("kspTest", libs.findLibrary("hilt.compiler").get())
                add("implementation", libs.findLibrary("hilt.android").get())
                add("testImplementation", libs.findLibrary("hilt.android.testing").get())
                add("implementation", libs.findLibrary("androidx.hilt.navigation.compose").get())
            }
        }
    }
}
