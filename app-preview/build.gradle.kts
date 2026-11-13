plugins {
    id("starter.android.application")
    id("starter.android.compose")
    id("starter.android.screenshot")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.citytexi.pushlog.preview"
    defaultConfig {
        applicationId = "com.citytexi.pushlog.preview"
    }
}

// Debug-only tool app: it is never shipped, so the release variant is switched off instead of
// removing the build type, which would clash with the shared application convention.
androidComponents {
    beforeVariants(selector().withBuildType("release")) { it.enable = false }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
}
