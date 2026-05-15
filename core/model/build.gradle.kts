plugins {
    alias(libs.plugins.treetask.android.library)
    alias(libs.plugins.treetask.android.desugar)
    // lint
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
}
android {
    namespace = "com.doannd3.treetask.core.model"
    resourcePrefix = "model_"
}
dependencies {

}
