plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}
android {
    namespace = "com.doannd3.treetask.core.data"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
        missingDimensionStrategy("environment", "dev")
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true // giúp Android 7 sài java.time
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}
dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:domain"))
    implementation(project(":core:common"))
    implementation(project(":core:network"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.paging.runtime)
    implementation(libs.work.runtime.ktx)
    implementation(libs.timber)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
