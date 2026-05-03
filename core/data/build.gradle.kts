plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    // lint
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
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
    // core
    implementation(projects.core.model)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.network)

    implementation(libs.androidx.core.ktx)
    implementation(libs.paging.runtime)
    implementation(libs.work.runtime.ktx)
    implementation(libs.timber)

    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.work)

    // java
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // testing
    testImplementation(projects.core.testing)
}
