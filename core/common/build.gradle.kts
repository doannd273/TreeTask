plugins {
    alias(libs.plugins.treetask.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
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

    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // desugar
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
