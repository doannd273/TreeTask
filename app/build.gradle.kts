plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    // google, firebase
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    // lint
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
}

android {
    namespace = "com.treestudio.treetask"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.treestudio.treetask"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            isShrinkResources = false
        }

        release {
            isDebuggable = false
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            resValue("string", "app_name", "TreeTask (DEV)") // tên app ngoài màn hình chính
        }
        create("prod") {
            dimension = "environment"
            resValue("string", "app_name", "TreeTask")
        }
    }
}

dependencies {
    // feature
    implementation(projects.feature.auth)
    implementation(projects.feature.chat)
    implementation(projects.feature.profile)
    implementation(projects.feature.stats)
    implementation(projects.feature.tasks)

// core
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.designsystem)
    implementation(projects.core.datastore)
    implementation(projects.core.model)
    implementation(projects.core.analytics)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // navigation compose
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // # UI & Utilities
    debugImplementation(libs.leakcanary)
    implementation(libs.timber)

    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // java
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.androidx.core.splashscreen)

    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
}
