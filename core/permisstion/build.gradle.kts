plugins {
    alias(libs.plugins.treetask.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)

    // lint
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
}

android {
    namespace = "com.doannd3.treetask.permisstion"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // hilt
    api(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // timber
    implementation(libs.timber)
}
