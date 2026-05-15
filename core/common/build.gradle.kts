plugins {
    alias(libs.plugins.treetask.android.library)
    alias(libs.plugins.treetask.android.hilt)
    alias(libs.plugins.treetask.android.desugar)
    // lint
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
}
android {
    namespace = "com.doannd3.treetask.core.common"
    resourcePrefix = "common_"
}
dependencies {
    // android
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // timber
    implementation(libs.timber)
}
