import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.exclude
import ru.resodostudios.cashsense.libs

class AndroidApplicationFirebaseConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.gms.google-services")
                apply("com.google.firebase.firebase-perf")
                apply("com.google.firebase.crashlytics")
            }

            dependencies {
                val bom = libs.findLibrary("firebase-bom").get()
                add("implementation", platform(bom))
                "implementation"(libs.findLibrary("firebase.analytics").get())
                "implementation"(libs.findLibrary("firebase.performance").get()) {
                    exclude(group = "com.google.protobuf", module = "protobuf-javalite")
                    exclude(group = "com.google.firebase", module = "protolite-well-known-types")
                }
                "implementation"(libs.findLibrary("firebase.crashlytics").get())
            }
        }
    }
}
