package de.mm20.launcher2.plugin.here.api

import kotlinx.serialization.Serializable

@Serializable
data class HPlace(
    val name: String?,
    val type: String?,
    val location: HLocation?,
    val id: String?,
)