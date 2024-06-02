package de.mm20.launcher2.plugin.here

import android.graphics.Color
import android.util.Log
import de.mm20.launcher2.plugin.config.QueryPluginConfig
import de.mm20.launcher2.plugin.config.StorageStrategy
import de.mm20.launcher2.plugin.here.api.HDeparture
import de.mm20.launcher2.plugin.here.api.HIn
import de.mm20.launcher2.plugin.here.api.HTransitBoard
import de.mm20.launcher2.sdk.base.GetParams
import de.mm20.launcher2.sdk.base.SearchParams
import de.mm20.launcher2.sdk.locations.Location
import de.mm20.launcher2.sdk.locations.LocationProvider
import de.mm20.launcher2.sdk.locations.LocationQuery
import de.mm20.launcher2.search.location.Departure
import de.mm20.launcher2.search.location.LineType
import de.mm20.launcher2.search.location.LocationIcon
import java.time.Duration

class HereDeparturesProvider : LocationProvider(
    config = QueryPluginConfig(
        storageStrategy = StorageStrategy.Deferred,
    )
) {
    private lateinit var apiClient: HereApiClient

    override fun onCreate(): Boolean {
        apiClient = HereApiClient(context!!.getString(R.string.api_key))
        return true
    }

    override suspend fun get(id: String, params: GetParams): Location? {
        return apiClient.transitDepartures(
            ids = setOf(id),
        ).boards
            ?.firstOrNull()
            ?.toLocation()

    }

    override suspend fun search(query: LocationQuery, params: SearchParams): List<Location> {
        if (!params.allowNetwork) return emptyList()

        val results = apiClient.transitStations(
            `in` = HIn(
                lat = query.userLatitude,
                lon = query.userLongitude,
                r = query.searchRadius.toInt(),
            ),
            name = query.query,
        )

        val stationIds = results.stations?.mapNotNull { it.place?.id } ?: return emptyList()

        val departures = apiClient.transitDepartures(
            ids = stationIds.toSet(),
        )
        return departures.boards?.mapNotNull { it.toLocation() } ?: emptyList()
    }
}

private fun HTransitBoard.toLocation(): Location? {

    val departures = departures?.mapNotNull {
        it.toDeparture()
    }
    return Location(
        id = place?.id ?: return null,
        label = place.name ?: return null,
        latitude = place.location?.lat ?: return null,
        longitude = place.location.lng ?: return null,
        category = null,
        departures = departures,
        icon = when {
            departures == null -> null
            departures.any { it.type == LineType.Airplane } -> LocationIcon.Airport
            departures.any { it.type == LineType.CableCar } -> LocationIcon.CableCar
            departures.any {
                it.type == LineType.Train || it.type == LineType.HighSpeedTrain || it.type == LineType.CommuterTrain
                        || it.type == LineType.Monorail
            } -> LocationIcon.Train

            departures.any { it.type == LineType.Tram } -> LocationIcon.Tram
            departures.any { it.type == LineType.Bus } -> LocationIcon.Bus
            else -> null
        }
    )
}

private fun HDeparture.toDeparture(): Departure? {
    return Departure(
        time = time ?: return null,
        line = transport?.name ?: return null,
        lastStop = transport.headsign ?: return null,
        delay = delay?.let { Duration.ofSeconds(it) },
        lineColor = parseColor(transport.color),
        type = when (transport.mode) {
            "highSpeedTrain" -> LineType.HighSpeedTrain
            "intercityTrain", "interRegionalTrain", "regionalTrain" -> LineType.Train
            "cityTrain" -> LineType.CommuterTrain
            "bus", "privateBus", "busRapid" -> LineType.Bus
            "ferry" -> LineType.Boat
            "subway" -> LineType.Subway
            "lightRail" -> LineType.Tram
            "aerial" -> LineType.CableCar
            "inclined", "monorail" -> LineType.Monorail
            "flight" -> LineType.Airplane
            else -> null
        },
    )
}

private fun parseColor(color: String?): Color? {
    if (color == null) return null
    return try {
        Color.valueOf(Color.parseColor(color))
    } catch (e: IllegalArgumentException) {
        null
    }
}

