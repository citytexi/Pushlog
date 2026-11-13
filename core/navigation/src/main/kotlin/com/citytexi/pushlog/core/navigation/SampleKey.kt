package com.citytexi.pushlog.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Back stack key of the sample screen. A key must be `@Serializable`: `rememberNavBackStack`
 * saves it across process death. `consumer-rules.pro` keeps every `NavKey` class for the
 * reflective serializer lookup.
 */
@Serializable
data object SampleKey : NavKey
