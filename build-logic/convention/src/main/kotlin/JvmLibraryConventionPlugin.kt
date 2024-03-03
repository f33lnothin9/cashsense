import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.resodostudios.cashsense.configureKotlinJvm

class JvmLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
            }
            configureKotlinJvm()
        }
    }
}
