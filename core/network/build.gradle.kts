import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}
android {
    namespace = "com.doannd3.treetask.core.network"
    compileSdk = 36
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }


    // Đọc file local.properties để lấy URL động
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(localPropertiesFile.inputStream())
    }

    flavorDimensions += "environment"
    // Tạo các môi trường biệt lập
    productFlavors {
        create("dev") {
            dimension = "environment"
            val localIp = properties.getProperty("DEV_BASE_API_URL") ?: "\"http://localhost:3000/\""
            buildConfigField("String", "BASE_URL", localIp)
        }
        create("prod") {
            dimension = "environment"
            val prodUrl = properties.getProperty("PROD_BASE_API_URL") ?: "\"https://api.treetask.domain.com/\""
            buildConfigField("String", "BASE_URL", prodUrl)
        }
    }
}
dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:datastore"))

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
    // implementation(libs.socket.io)
    debugImplementation(libs.chucker.debug)
    releaseImplementation(libs.chucker.release)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.timber)
}
