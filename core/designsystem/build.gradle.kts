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
    // lerp를 직접 쓴다. BOM이 버전을 정한다.
    implementation(libs.androidx.compose.ui.util)
}
