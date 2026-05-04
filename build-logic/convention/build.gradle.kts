plugins {
    `kotlin-dsl`
}
// Khai báo các plugin mà convention này sẽ bọc lại
dependencies {
    compileOnly(libs.detekt.gradle)
    compileOnly(libs.spotless.gradle)
    // library plugin android
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidDetekt") {
            id = "treetask.android.detekt"
            implementationClass = "AndroidDetektConventionPlugin"
        }
        register("androidSpotless") {
            id = "treetask.android.spotless"
            implementationClass = "AndroidSpotlessConventionPlugin"
        }
        register("androidLibrary") {
            id = "treetask.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidApplication") {
            id = "treetask.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
    }
}
