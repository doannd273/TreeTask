plugins {
    alias(libs.plugins.treetask.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    // lint
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
}
android {
    namespace = "com.doannd3.treetask.core.testing"
}
dependencies {
    api(projects.core.model)
    api(projects.core.model)

    api(libs.junit)
    api(libs.androidx.junit)
    api(libs.androidx.espresso.core)

    // Testing weapons
    api(libs.mockk)
    api(libs.turbine)
    api(libs.truth)
    api(libs.kotlinx.coroutines.test)

    api(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
