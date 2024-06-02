package de.mm20.launcher2.plugin.here.api

import de.mm20.launcher2.plugin.here.api.serializer.ZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
data class HDeparture(
    @Serializable(with = ZonedDateTimeSerializer::class)
    val time: ZonedDateTime?,
    val delay: Long?,
    val status: String?,
    val transport: HTransport?,
    val agency: HAgency?,
)

@Serializable
data class HTransport(
    val mode: String?,
    val name: String?,
    val category: String?,
    val color: String?,
    val textColor: String?,
    val headsign: String?,
)

@Serializable
data class HAgency(
    val id: String?,
    val name: String?,
    val website: String?,
)