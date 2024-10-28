plugins {
    alias(libs.plugins.custom.android.library)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.yjy.data.datastore_api"
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

androidComponents.beforeVariants { variant ->
    android.sourceSets.getByName(variant.name).apply {
        val buildDir = layout.buildDirectory.get().asFile
        java.srcDir(buildDir.resolve("generated/source/proto/${variant.name}/java"))
        kotlin.srcDir(buildDir.resolve("generated/source/proto/${variant.name}/kotlin"))
    }
}

dependencies {
    api(libs.protobuf.kotlin.lite)
    implementation(libs.kotlinx.coroutines.core)
}
