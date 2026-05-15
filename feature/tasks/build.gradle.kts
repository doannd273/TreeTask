plugins {
    alias(libs.plugins.treetask.android.library)
    alias(libs.plugins.treetask.android.compose)
    alias(libs.plugins.treetask.android.desugar)

    alias(libs.plugins.kotlin.serialization)
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
}
dependencies {
    // core
    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.designsystem)
    implementation(projects.core.analytics)
    implementation(projects.core.datastore)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)

    implementation(libs.timber)
}
