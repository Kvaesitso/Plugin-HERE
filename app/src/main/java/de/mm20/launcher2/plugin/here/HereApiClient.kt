package de.mm20.launcher2.plugin.here

import android.content.Context
import android.util.Log
import de.mm20.launcher2.plugin.foursquare.dataStore
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
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class HereApiClient(
    private val context: Context,
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
        apiKey: String? = null,
    ): HTransitStations {
        val apiKey = apiKey ?: this.apiKey.first() ?: throw IllegalArgumentException("No API key provided")
        val response = client.get {
            url {
                host = "transit.hereapi.com"
                path("v8", "stations")
                parameter("apiKey", apiKey)
                parameter("in", `in`.toString())
                parameter("name", name)
            }
        }
        if (response.status == HttpStatusCode.Unauthorized) {
            throw IllegalArgumentException("Unauthorized. Invalid API key?; body ${response.bodyAsText()}")
        } else if (response.status != HttpStatusCode.OK) {
            throw IOException("API error: status ${response.status.value}; body ${response.bodyAsText()}")
        }
        return response.body()
    }

    suspend fun transitDepartures(
        ids: Set<String>,
        apiKey: String? = null,
    ): HTransitDepartures {
        val apiKey = apiKey ?: this.apiKey.first() ?: throw IllegalArgumentException("No API key provided")
        val response = client.get {
            url {
                host = "transit.hereapi.com"
                path("v8", "departures")
                parameter("apiKey", apiKey)
                parameter("ids", ids.joinToString(","))
            }
        }
        if (response.status == HttpStatusCode.Unauthorized) {
            throw IllegalArgumentException("Unauthorized. Invalid API key?; body ${response.bodyAsText()}")
        } else if (response.status != HttpStatusCode.OK) {
            throw IOException("API error: status ${response.status.value}; body ${response.bodyAsText()}")
        }
        return response.body()
    }

    suspend fun discover(
        at: HPosition,
        q: String,
        lang: String?,
        apiKey: String? = null,
    ): HDiscover {
        val apiKey = apiKey ?: this.apiKey.first() ?: throw IllegalArgumentException("No API key provided")
        val response = client.get {
            url {
                host = "discover.search.hereapi.com"
                path("v1", "discover")
                parameter("apiKey", apiKey)
                parameter("at", at.toString())
                parameter("q", q)
                if (lang != null) parameter("lang", lang)
            }
        }
        if (response.status == HttpStatusCode.Unauthorized) {
            throw IllegalArgumentException("Unauthorized. Invalid API key?; body ${response.bodyAsText()}")
        } else if (response.status != HttpStatusCode.OK) {
            throw IOException("API error: status ${response.status.value}; body ${response.bodyAsText()}")
        }
        return response.body()
    }

    suspend fun lookup(
        id: String,
        lang: String?,
        apiKey: String? = null,
    ): HDiscoverItem {
        val apiKey = apiKey ?: this.apiKey.first() ?: throw IllegalArgumentException("No API key provided")
        val response = client.get {
            url {
                host = "lookup.search.hereapi.com"
                path("v1", "lookup")
                parameter("apiKey", apiKey)
                parameter("id", id)
                if (lang != null) parameter("lang", lang)
            }
        }
        if (response.status == HttpStatusCode.Unauthorized) {
            throw IllegalArgumentException("Unauthorized. Invalid API key?; body ${response.bodyAsText()}")
        } else if (response.status != HttpStatusCode.OK) {
            throw IOException("API error: status ${response.status.value}; body ${response.bodyAsText()}")
        }
        return response.body()
    }

    suspend fun weatherReport(
        location: HLocation,
        products: Set<String>,
        oneObservation: Boolean = false,
        lang: String? = null,
        units: String? = null,
        apiKey: String? = null,
    ): HWeatherReport {
        val apiKey = apiKey ?: this.apiKey.first() ?: throw IllegalArgumentException("No API key provided")
        val response = client.get {
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
        }
        if (response.status == HttpStatusCode.Unauthorized) {
            throw IllegalArgumentException("Unauthorized. Invalid API key?; body ${response.bodyAsText()}")
        } else if (response.status != HttpStatusCode.OK) {
            throw IOException("API error: status ${response.status.value}; body ${response.bodyAsText()}")
        }
        return response.body()
    }

    suspend fun setApiKey(apiKey: String) {
        context.dataStore.updateData {
            it.copy(apiKey = apiKey)
        }
    }

    suspend fun testApiKey(apiKey: String): Boolean {
        return try {
            weatherReport(
                HLocation(lat = 51.5, lng = 0.0),
                products = setOf("observation"),
                apiKey = apiKey
            )
            return true
        } catch (e: IllegalArgumentException) {
            Log.e("HereApiClient", "Invalid API key", e)
            return false
        }
    }

    val apiKey: Flow<String?> = context.dataStore.data.map { it.apiKey }

}