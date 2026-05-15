plugins {
    alias(libs.plugins.treetask.android.library)
    alias(libs.plugins.treetask.android.hilt)
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

    // paging
    implementation(libs.paging.runtime)

    // testing
    testImplementation(projects.core.testing)
    testImplementation(kotlin("test"))
}
