package com.citytexi.pushlog.feature.sample

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.domain.model.SampleItem
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

/**
 * Error snackbars on the stateless [SampleScreen]. A snackbar has to be shown from a `LaunchedEffect`,
 * which a preview cannot do; the other states are previews in `SampleScreen.kt`, captured automatically.
 * `SampleRoute` needs Hilt and is not captured.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SampleScreenScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    // Errors are SampleSideEffect.ShowError: the Route shows them in a snackbar, as here.
    @Test
    fun networkError() = capture("sample_screen_error_network", SampleUiState(), SampleError.Network)

    @Test
    fun serverError() = capture("sample_screen_error_server", SampleUiState(), SampleError.Server)

    @Test
    fun unknownError() =
        capture(
            "sample_screen_error_unknown",
            SampleUiState(items = listOf(SampleItem(1, "First"))),
            SampleError.Unknown,
        )

    private fun capture(
        name: String,
        state: SampleUiState,
        error: SampleError? = null,
    ) {
        val snackbarHostState = SnackbarHostState()
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                SampleScreen(state = state, snackbarHostState = snackbarHostState, onIntent = {})
            }
            if (error != null) {
                val message = stringResource(error.messageRes)
                // Indefinite: the snackbar stays until captured (a timed one would be gone once idle).
                LaunchedEffect(message) {
                    snackbarHostState.showSnackbar(message, withDismissAction = true, duration = SnackbarDuration.Indefinite)
                }
            }
        }
        composeRule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }
}
