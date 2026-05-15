plugins {
    alias(libs.plugins.treetask.android.library)
    alias(libs.plugins.treetask.android.hilt)
    // lint
    alias(libs.plugins.treetask.android.detekt)
    alias(libs.plugins.treetask.android.spotless)
}
android {
    namespace = "com.doannd3.treetask.core.database"
    resourcePrefix = "database_"
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
dependencies {
    // room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    ksp(libs.room.compiler)
}
