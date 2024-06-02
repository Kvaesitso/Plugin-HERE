package de.mm20.launcher2.plugin.here.api

import kotlinx.serialization.Serializable

@Serializable
data class HPlaceResult(
    val place: HPlace?
)