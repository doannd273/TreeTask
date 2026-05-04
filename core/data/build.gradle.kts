plugins {
    alias(libs.plugins.treetask.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    // lint
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
}
android {
    namespace = "com.doannd3.treetask.core.data"
    defaultConfig {
        missingDimensionStrategy("environment", "dev")
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}
dependencies {
    // core
    implementation(projects.core.model)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.network)

    implementation(libs.paging.runtime)
    implementation(libs.work.runtime.ktx)
    implementation(libs.timber)

    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.work)

    // java
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // testing
    testImplementation(projects.core.testing)
}
