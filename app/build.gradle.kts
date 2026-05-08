import java.util.Properties

plugins {
    alias(libs.plugins.treetask.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    // google, firebase
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.perf)
    // lint
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
}

android {
    namespace = "com.treestudio.treetask"

    // Đọc file local.properties để lấy URL động
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(localPropertiesFile.inputStream())
    }

    defaultConfig {
        applicationId = "com.treestudio.treetask"
        // versionCode = tổng số commit (tự tăng mỗi lần có commit mới)
        versionCode =
            providers
                .exec {
                    commandLine("git", "rev-list", "--count", "HEAD")
                }.standardOutput.asText
                .get()
                .trim()
                .toInt()

        // versionName = tên Git Tag gần nhất (ví dụ: "1.0.0")
        versionName =
            providers
                .exec {
                    commandLine("git", "tag", "--sort=-v:refname")
                }.standardOutput.asText
                .get()
                .trim()
                .lines()
                .firstOrNull() ?: "1.0.0"

        // Chỉ bundle ngôn ngữ EN và VI, loại bỏ các bản dịch thừa từ thư viện bên thứ 3
        @Suppress("DEPRECATION")
        resourceConfigurations.addAll(listOf("en", "vi"))

        ndk {
            // Chỉ lấy các kiến trúc chip điện thoại phổ biến nhất hiện nay
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }
    }

    signingConfigs {
        create("release") {
            // Ưu tiên local.properties, nếu không có thì lấy từ Environment Variable (cho CI)
            val keystorePath =
                properties.getProperty("signing.storeFilePath")
                    ?: System.getenv("RELEASE_KEYSTORE_PATH")

            if (keystorePath != null && keystorePath.isNotEmpty()) {
                storeFile = file(keystorePath)
            }

            storePassword = properties.getProperty("signing.storePassword")
                ?: System.getenv("RELEASE_KEYSTORE_PASSWORD")

            keyAlias = properties.getProperty("signing.keyAlias")
                ?: System.getenv("RELEASE_KEY_ALIAS")

            keyPassword = properties.getProperty("signing.keyPassword")
                ?: System.getenv("RELEASE_KEY_PASSWORD")
        }
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

            signingConfig = signingConfigs.getByName("release") // Gắn cấu hình ký vào bản release
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            // Bổ sung dòng này để Crashlytics upload file mapping
            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                mappingFileUploadEnabled = true
            }
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
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

            buildConfigField("String", "ENV", "\"DEV\"")
            buildConfigField("Boolean", "IS_DEV", "true")
        }
        create("prod") {
            dimension = "environment"
            resValue("string", "app_name", "TreeTask")

            buildConfigField("String", "ENV", "\"PROD\"")
            buildConfigField("Boolean", "IS_DEV", "false")
        }
    }

    // Đặt tên app đầu ra cho dễ nhớ, app release
    applicationVariants.all {
        outputs.all {
            val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            val fileName = "TreeTask_${versionName}_$versionCode.apk"
            output.outputFileName = fileName
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
    implementation(projects.core.data)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // testing
    testImplementation(projects.core.testing)
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
    implementation(libs.androidx.hilt.work)

    // java
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.androidx.core.splashscreen)

    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)
}
