plugins {
    id("starter.android.library")
    id("starter.android.compose")
}

android {
    namespace = "com.citytexi.pushlog.core.ui"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    // MviContainer.collectAsStateWithLifecycle() and collectSideEffect { }.
    implementation(libs.androidx.lifecycle.runtime.compose)
}
