plugins {
    alias(libs.plugins.treetask.android.library)
    // lint
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
}
android {
    namespace = "com.doannd3.treetask.core.testing"
    resourcePrefix = "testing_"
}
dependencies {
    api(libs.junit)
    api(libs.mockk)
    api(libs.turbine)
    api(libs.truth)
    api(libs.kotlinx.coroutines.test)
}
