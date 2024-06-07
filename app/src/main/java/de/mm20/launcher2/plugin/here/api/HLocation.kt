package de.mm20.launcher2.plugin.here.api

import kotlinx.serialization.Serializable

@Serializable
data class HLocation(
    val lat: Double?,
    val lng: Double?,
) {
    override fun toString(): String {
        return "$lat,$lng"
    }
}