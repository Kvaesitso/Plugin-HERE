package de.mm20.launcher2.plugin.here

import de.mm20.launcher2.plugin.config.WeatherPluginConfig
import de.mm20.launcher2.plugin.here.api.HLocation
import de.mm20.launcher2.plugin.here.api.HWeatherForecast
import de.mm20.launcher2.plugin.here.api.HWeatherObservation
import de.mm20.launcher2.plugin.here.api.HWeatherPlace
import de.mm20.launcher2.sdk.weather.C
import de.mm20.launcher2.sdk.weather.Forecast
import de.mm20.launcher2.sdk.weather.WeatherLocation
import de.mm20.launcher2.sdk.weather.WeatherProvider
import de.mm20.launcher2.sdk.weather.km_h
import de.mm20.launcher2.sdk.weather.mm
import de.mm20.launcher2.weather.WeatherIcon

class HereWeatherProvider : WeatherProvider(
    WeatherPluginConfig()
) {
    private lateinit var apiClient: HereApiClient

    override fun onCreate(): Boolean {
        apiClient = HereApiClient(context!!.getString(R.string.api_key))
        return true
    }

    override suspend fun getWeatherData(lat: Double, lon: Double, lang: String?): List<Forecast>? {
        return getWeatherData(null, lat, lon, lang)
    }

    override suspend fun getWeatherData(location: WeatherLocation, lang: String?): List<Forecast>? {
        if (location is WeatherLocation.LatLon) {
            return getWeatherData(
                location.name,
                location.lat,
                location.lon,
                lang,
            )
        }
        return null
    }

    private suspend fun getWeatherData(
        name: String?,
        lat: Double,
        lon: Double,
        lang: String?,
    ): List<Forecast>? {
        val creationTime = System.currentTimeMillis()
        val data = apiClient.weatherReport(
            location = HLocation(
                lat = lat,
                lng = lon,
            ),
            products = setOf("observation", "forecastHourly"),
            // HERE expects language to be in the format `en-XX`,
            // but if the region part is invalid, it just falls back to the default variant of the language.
            // However, if we just pass the language code without the region part, it will return an error.
            lang = "$lang-XX",
            oneObservation = true,
        )
        val observation = data.places
            ?.firstOrNull { it.observations != null }
            ?.observations
            ?.firstOrNull()
            ?.toForecast(creationTime, name)

        val forecasts = data.places
            ?.firstOrNull { it.hourlyForecasts != null }
            ?.hourlyForecasts
            ?.takeIf { it.isNotEmpty() }
            ?.firstOrNull() ?: return null


        return buildList {
            observation?.let { add(it) }
            for (forecast in forecasts.forecasts ?: return null) {
                forecast.toForecast(
                    creationTime,
                    data.places.firstOrNull()?.observations?.firstOrNull()?.place ?: return null,
                    name
                )?.let {
                    add(it)
                }
            }
        }
    }
}

private fun HWeatherObservation.toForecast(createdAt: Long, locationName: String?): Forecast? {
    return Forecast(
        timestamp = time?.toEpochSecond()?.times(1000L) ?: return null,
        location = locationName ?: place?.address?.let {
            "${it.city}, ${it.countryCode}"
        } ?: return null,
        condition = precipitationDesc.takeIf { !it.isNullOrEmpty() }
            ?: skyDesc.takeIf { !it.isNullOrEmpty() }
            ?: temperatureDesc.takeIf { !it.isNullOrEmpty() }
            ?: description ?: return null,
        icon = getIcon(iconId ?: return null),
        temperature = temperature?.C ?: return null,
        humidity = humidity?.toDoubleOrNull()?.toInt(),
        minTemp = lowTemperature?.toDoubleOrNull()?.C,
        maxTemp = highTemperature?.toDoubleOrNull()?.C,
        windSpeed = windSpeed?.km_h,
        windDirection = windDirection,
        night = daylight == "night",
        createdAt = createdAt,
        precipitation = precipitation1H?.times(10)?.mm,
        rainProbability = precipitationProbability,

        provider = "HERE",
    )
}

private fun HWeatherForecast.toForecast(
    createdAt: Long,
    place: HWeatherPlace,
    locationName: String?
): Forecast? {
    return Forecast(
        timestamp = time?.toEpochSecond()?.times(1000L) ?: return null,
        location = locationName ?: place.address?.let {
            "${it.city}, ${it.countryCode}"
        } ?: return null,
        condition = precipitationDesc.takeIf { !it.isNullOrEmpty() }
            ?: skyDesc.takeIf { !it.isNullOrEmpty() }
            ?: temperatureDesc.takeIf { !it.isNullOrEmpty() }
            ?: description ?: return null,
        icon = getIcon(iconId ?: return null),
        temperature = temperature?.C ?: return null,
        humidity = humidity?.toDoubleOrNull()?.toInt(),
        minTemp = lowTemperature?.toDoubleOrNull()?.C,
        maxTemp = highTemperature?.toDoubleOrNull()?.C,
        windSpeed = windSpeed?.km_h,
        windDirection = windDirection,
        night = daylight == "night",
        createdAt = createdAt,
        precipitation = precipitation1H?.times(10)?.mm,
        rainProbability = precipitationProbability,

        provider = "HERE",
    )
}

private fun getIcon(iconId: Int): WeatherIcon {
    return when (iconId) {
        1, 13 -> WeatherIcon.Clear
        2, 4, 14, 15 -> WeatherIcon.PartlyCloudy
        3 -> WeatherIcon.Haze
        5, 16 -> WeatherIcon.BrokenClouds
        6, 17 -> WeatherIcon.MostlyCloudy
        7 -> WeatherIcon.Cloudy
        8, 9, 10, 12 -> WeatherIcon.Fog
        11, 25, 26 -> WeatherIcon.Storm
        18, 34 -> WeatherIcon.Drizzle
        19, 20, 32, 33 -> WeatherIcon.Showers
        21, 22, 35 -> WeatherIcon.Thunderstorm
        23 -> WeatherIcon.HeavyThunderstorm
        24 -> WeatherIcon.Hail
        27, 28 -> WeatherIcon.Sleet
        29, 30, 31 -> WeatherIcon.Snow
        else -> WeatherIcon.Unknown
    }
}