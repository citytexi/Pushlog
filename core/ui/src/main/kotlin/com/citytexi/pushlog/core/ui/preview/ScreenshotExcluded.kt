package com.citytexi.pushlog.core.ui.preview

/**
 * Keeps a preview out of the automatic screenshot capture (`starter.android.screenshot`).
 *
 * For previews Robolectric cannot render (Hilt, ViewModel) or that differ on every run (time, random).
 * The build logic refers to this class by its fully qualified name; move or rename both together.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class ScreenshotExcluded
