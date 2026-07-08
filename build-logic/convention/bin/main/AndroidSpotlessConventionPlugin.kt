import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

class AndroidSpotlessConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        pluginManager.apply("com.diffplug.spotless")
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        val ktlintVersion = libs.findVersion("ktlintFormatter").get().requiredVersion
        val extension = extensions.getByType(SpotlessExtension::class.java)
        extension.apply {
            kotlin {
                target("**/*.kt")
                targetExclude("**/build/**/*.kt")
                ktlint(ktlintVersion).editorConfigOverride(ktlintEditorConfigOverride)
            }
            kotlinGradle {
                target("*.gradle.kts")
                ktlint(ktlintVersion).editorConfigOverride(ktlintEditorConfigOverride)
            }
        }
    }
}

private val ktlintEditorConfigOverride =
    mapOf(
        "ktlint_code_style" to "intellij_idea",
        // Keep constructor-injected class headers readable and stable.
        // These rules were the main source of overly deep indentation around
        // `class Foo @Inject constructor(...)` in ViewModel/UseCase-heavy files.
        "ktlint_standard_annotation" to "disabled",
        "ktlint_standard_argument-list-wrapping" to "disabled",
        "ktlint_standard_binary-expression-wrapping" to "disabled",
        "ktlint_standard_blank-line-between-when-conditions" to "disabled",
        "ktlint_standard_chain-method-continuation" to "disabled",
        "ktlint_standard_class-signature" to "disabled",
        "ktlint_standard_condition-wrapping" to "disabled",
        "ktlint_standard_function-expression-body" to "disabled",
        "ktlint_standard_function-literal" to "disabled",
        "ktlint_standard_function-signature" to "disabled",
        "ktlint_standard_max-line-length" to "disabled",
        "ktlint_standard_multiline-loop" to "disabled",
        "ktlint_standard_parameter-list-wrapping" to "disabled",
        "ktlint_standard_property-wrapping" to "disabled",
        // Intentionally keep `ktlint_standard_indent` enabled so ktlint can
        // reflow bodies correctly after headers are normalized.
    )
