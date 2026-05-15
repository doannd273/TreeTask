plugins {
    alias(libs.plugins.treetask.android.library)
    alias(libs.plugins.treetask.android.compose)

    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    // lint
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
}
android {
    namespace = "com.doannd3.treetask.feature.profile"
    resourcePrefix = "profile_"
    defaultConfig {
        missingDimensionStrategy("environment", "dev")
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}
dependencies {
    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.designsystem)
    implementation(projects.core.analytics)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.coil.compose)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.timber)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
