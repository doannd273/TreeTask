plugins {
    alias(libs.plugins.treetask.android.library)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    // lint
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
}
android {
    namespace = "com.doannd3.treetask.core.database"
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
dependencies {
    implementation(projects.core.model)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    ksp(libs.room.compiler)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
