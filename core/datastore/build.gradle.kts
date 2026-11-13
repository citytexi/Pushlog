plugins {
    id("starter.android.library")
    id("starter.hilt")
}

android {
    namespace = "com.citytexi.pushlog.core.datastore"
}

dependencies {
    api(project(":core:common"))
    api(libs.androidx.datastore.preferences)
    api(libs.kotlinx.coroutines.core)

    testImplementation(project(":core:testing:android"))
}
