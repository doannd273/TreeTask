plugins {
    alias(libs.plugins.treetask.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    // lint
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
}
android {
    namespace = "com.doannd3.treetask.core.domain"
    resourcePrefix = "domain_"
}
dependencies {
    // core
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.datastore)

    // paging
    implementation(libs.paging.runtime)

    // / hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // testing
    testImplementation(projects.core.testing)
    testImplementation(kotlin("test"))
}
