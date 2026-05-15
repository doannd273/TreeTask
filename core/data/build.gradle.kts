plugins {
    alias(libs.plugins.treetask.android.library)
    alias(libs.plugins.treetask.android.hilt)
    alias(libs.plugins.treetask.android.desugar)
    // lint
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
}
android {
    namespace = "com.doannd3.treetask.core.data"
    resourcePrefix = "data_"
    defaultConfig {
        missingDimensionStrategy("environment", "dev")
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

    implementation(libs.retrofit)
    implementation(libs.room.ktx)
    implementation(libs.paging.runtime)
    implementation(libs.work.runtime.ktx)
    implementation(libs.timber)

    // hilt
    implementation(libs.androidx.hilt.work)

    // testing
    testImplementation(projects.core.testing)
}
