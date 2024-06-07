package de.mm20.launcher2.plugin.here

import de.mm20.launcher2.plugin.here.api.HDiscover
import de.mm20.launcher2.plugin.here.api.HDiscoverItem
import de.mm20.launcher2.plugin.here.api.HLocation
import de.mm20.launcher2.plugin.here.api.HIn
import de.mm20.launcher2.plugin.here.api.HPosition
import de.mm20.launcher2.plugin.here.api.HTransitDepartures
import de.mm20.launcher2.plugin.here.api.HTransitStations
import de.mm20.launcher2.plugin.here.api.HWeatherReport
import de.mm20.launcher2.serialization.Json
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json

class HereApiClient(
    private val apiKey: String,
) {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json.Lenient)
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
            }
        }
    }

    suspend fun transitStations(
        `in`: HIn,
        name: String,
    ): HTransitStations {
        return client.get {
            url {
                host = "transit.hereapi.com"
                path("v8", "stations")
                parameter("apiKey", apiKey)
                parameter("in", `in`.toString())
                parameter("name", name)
            }
        }.body()
    }

    suspend fun transitDepartures(
        ids: Set<String>,
    ): HTransitDepartures {
        return client.get {
            url {
                host = "transit.hereapi.com"
                path("v8", "departures")
                parameter("apiKey", apiKey)
                parameter("ids", ids.joinToString(","))
            }
        }.body()
    }

    suspend fun discover(
        at: HPosition,
        q: String,
        lang: String?,
    ): HDiscover {
        return client.get {
            url {
                host = "discover.search.hereapi.com"
                path("v1", "discover")
                parameter("apiKey", apiKey)
                parameter("at", at.toString())
                parameter("q", q)
                if (lang != null) parameter("lang", lang)
            }
        }.body()
    }

    suspend fun lookup(
        id: String,
        lang: String?,
    ): HDiscoverItem {
        return client.get {
            url {
                host = "lookup.search.hereapi.com"
                path("v1", "lookup")
                parameter("apiKey", apiKey)
                parameter("id", id)
                if (lang != null) parameter("lang", lang)
            }
        }.body()
    }

    suspend fun weatherReport(
        location: HLocation,
        products: Set<String>,
        oneObservation: Boolean = false,
        lang: String? = null,
        units: String? = null,
    ): HWeatherReport {
        return client.get {
            url {
                host = "weather.hereapi.com"
                path("v3", "report")
                parameter("apiKey", apiKey)
                parameter("location", location.toString())
                parameter("products", products.joinToString(","))
                parameter("oneObservation", oneObservation.toString())
                if (lang != null) parameter("lang", lang)
                if (units != null) parameter("units", units)
            }
        }.body()
    }

}