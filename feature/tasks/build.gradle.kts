plugins {
    alias(libs.plugins.treetask.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    // lint
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
}
android {
    namespace = "com.doannd3.treetask.feature.tasks"
    resourcePrefix = "tasks_"
    defaultConfig {
        missingDimensionStrategy("environment", "dev")
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures { compose = true }
}
dependencies {
    // core
    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.core.common)
    implementation(projects.core.designsystem)
    implementation(projects.core.analytics)
    implementation(projects.core.datastore)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.timber)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
