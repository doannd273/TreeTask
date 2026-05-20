import java.util.Properties

plugins {
    alias(libs.plugins.treetask.android.library)
    alias(libs.plugins.treetask.android.hilt)
    alias(libs.plugins.kotlin.serialization)
    // lint
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
}
android {
    namespace = "com.doannd3.treetask.core.network"
    resourcePrefix = "network_"

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
    implementation(projects.core.common)
    implementation(projects.core.datastore)

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)

    // chucker
    debugImplementation(libs.chucker.debug)
    releaseImplementation(libs.chucker.release)

    // timber
    implementation(libs.timber)

    // testing
    testImplementation(projects.core.testing)
}
