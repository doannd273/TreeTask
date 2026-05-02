plugins {
    `kotlin-dsl`
}
// Khai báo các plugin mà convention này sẽ bọc lại
dependencies {
    compileOnly(libs.detekt.gradle)
    compileOnly(libs.spotless.gradle)
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
    }
}
