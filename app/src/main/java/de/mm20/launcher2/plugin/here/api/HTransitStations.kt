package de.mm20.launcher2.plugin.here.api

import kotlinx.serialization.Serializable

@Serializable
data class HTransitStations(
    val stations: List<HPlaceResult>?
)