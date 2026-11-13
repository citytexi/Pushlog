package com.citytexi.pushlog.preview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme

/** Launcher entry of the debug-only design-system preview app. */
class PreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PushlogTheme { }
        }
    }
}
