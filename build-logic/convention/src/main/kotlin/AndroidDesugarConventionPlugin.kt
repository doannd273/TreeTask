import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidDesugarConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        pluginManager.withPlugin("com.android.application") {
            extensions.configure<ApplicationExtension> {
                compileOptions {
                    isCoreLibraryDesugaringEnabled = true
                }
            }
        }

        pluginManager.withPlugin("com.android.library") {
            extensions.configure<LibraryExtension> {
                compileOptions {
                    isCoreLibraryDesugaringEnabled = true
                }
            }
        }

        dependencies {
            add("coreLibraryDesugaring", libs.findLibrary("desugar.jdk.libs").get())
        }
    }
}