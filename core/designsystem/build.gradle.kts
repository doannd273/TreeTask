plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // lint
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
}
android {
    namespace = "com.doannd3.treetask.core.designsystem"
    compileSdk = 36

    resourcePrefix = "designsystem_"

    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures { compose = true }
    kotlinOptions {
        jvmTarget = "17"
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.material3)
    debugApi(libs.androidx.compose.ui.tooling)
}
