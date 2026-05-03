plugins {
    alias(libs.plugins.treetask.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    // lint
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
}
android {
    namespace = "com.doannd3.treetask.core.analytics"
}
dependencies {
    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // timber
    implementation(libs.timber)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
