plugins {
    alias(libs.plugins.cashsense.android.library)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "ru.resodostudios.cashsense.core.datastore.proto"
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

androidComponents.beforeVariants {
    android.sourceSets.forEach {
        val buildDir = layout.buildDirectory.get().asFile
        it.java.srcDir(buildDir.resolve("generated/source/proto/${it.name}/java"))
        it.kotlin.srcDir(buildDir.resolve("generated/source/proto/${it.name}/kotlin"))
    }
}

dependencies {
    api(libs.protobuf.kotlin.lite)
}