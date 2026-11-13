package com.citytexi.pushlog.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SampleItemResponse(
    val id: Long,
    val title: String,
)
