plugins {
    alias(libs.plugins.treetask.android.library)
    alias(libs.plugins.treetask.android.compose)
    // lint
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
}
android {
    namespace = "com.doannd3.treetask.core.designsystem"
    resourcePrefix = "designsystem_"
}
dependencies {
}
