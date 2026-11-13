plugins {
    id("starter.android.library")
    id("starter.android.compose")
    id("starter.android.screenshot")
}

android {
    namespace = "com.citytexi.pushlog.core.designsystem"
}

dependencies {
    implementation(project(":core:ui"))
}
