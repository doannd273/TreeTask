import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidSpotlessConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.diffplug.spotless")
            val extension = extensions.getByType(SpotlessExtension::class.java)
            extension.apply {
                kotlin {
                    target("**/*.kt")
                    targetExclude("**/build/**/*.kt")
                    ktlint().editorConfigOverride(
                        mapOf(
                            "ktlint_function_naming_ignore_when_annotated_with" to "Composable"
                        )
                    )
                }
                kotlinGradle {
                    target("*.gradle.kts")
                    ktlint()
                }
            }
        }
    }
}