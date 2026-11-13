plugins {
    id("starter.android.library")
    id("starter.hilt")
}

android {
    namespace = "com.citytexi.pushlog.data"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(libs.androidx.room.ktx)

    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
}
