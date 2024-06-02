package de.mm20.launcher2.plugin.here.api

import kotlinx.serialization.Serializable

@Serializable
data class HTransitDepartures(
    val boards: List<HTransitBoard>?
)

@Serializable
data class HTransitBoard(
    val place: HPlace?,
    val departures: List<HDeparture>?
)