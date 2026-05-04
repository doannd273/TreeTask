import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidDetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.gitlab.arturbosch.detekt")
            val extension = extensions.getByType(DetektExtension::class.java)
            extension.apply {
                buildUponDefaultConfig = true
                // Chỉ đường dẫn tới file config của bạn
                config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
            }
        }
    }
}