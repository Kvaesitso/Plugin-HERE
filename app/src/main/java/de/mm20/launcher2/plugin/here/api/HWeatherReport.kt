package de.mm20.launcher2.plugin.here.api

import de.mm20.launcher2.plugin.here.api.serializer.ZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
data class HWeatherReport(
    val places: List<HWeatherReportItem>?
)

@Serializable
data class HWeatherReportItem(
    val observations: List<HWeatherObservation>?,
    val hourlyForecasts: List<HWeatherForecastItem>?,
)

@Serializable
data class HWeatherObservation(
    val place: HWeatherPlace?,
    val daylight: String?,
    val description: String?,
    val skyInfo: Int?,
    val skyDesc: String?,
    val temperature: Double?,
    val temperatureDesc: String?,
    val comfort: String?,
    val highTemperature: String?,
    val lowTemperature: String?,
    val humidity: String?,
    val dewPoint: Double?,
    val precipitation1H: Double?,
    val precipitation3H: Double?,
    val precipitation6H: Double?,
    val precipitation12H: Double?,
    val precipitation24H: Double?,
    val precipitationProbability: Int?,
    val precipitationDesc: String?,
    val rainFall: Double?,
    val snowFall: Double?,
    val airInfo: Int?,
    val airDesc: String?,
    val windSpeed: Double?,
    val windDirection: Double?,
    val windDesc: String?,
    val windDescShort: String?,
    val beaufortScale: Int?,
    val beaufortDesc: String?,
    val uvIndex: Int?,
    val uvDesc: String?,
    val barometerPressure: Double?,
    val barometerTrend: String?,
    val visibility: Double?,
    val snowCover: Double?,
    val iconId: Int?,
    val iconName: String?,
    val iconLink: String?,
    val ageMinutes: Int?,
    val activeAlerts: Int?,
    val weekday: String?,
    @Serializable(with = ZonedDateTimeSerializer::class)
    val time: ZonedDateTime?,
)

@Serializable
data class HWeatherPlace(
    val id: String?,
    val address: HAddress?,
    val location: HLocation?,
    val distance: Double?,
)

@Serializable
data class HWeatherForecastItem(
    val place: HWeatherPlace?,
    val forecasts: List<HWeatherForecast>?,
)

@Serializable
data class HWeatherForecast(
    val daylight: String?,
    val daySegment: String?,
    val description: String?,
    val skyInfo: Int?,
    val skyDesc: String?,
    val temperature: Double?,
    val temperatureDesc: String?,
    val comfort: String?,
    val highTemperature: String?,
    val lowTemperature: String?,
    val humidity: String?,
    val dewPoint: Double?,
    val precipitation1H: Double?,
    val precipitation3H: Double?,
    val precipitation6H: Double?,
    val precipitation12H: Double?,
    val precipitation24H: Double?,
    val precipitationProbability: Int?,
    val precipitationDesc: String?,
    val rainFall: Double?,
    val snowFall: Double?,
    val airInfo: Int?,
    val airDesc: String?,
    val windSpeed: Double?,
    val windDirection: Double?,
    val windDesc: String?,
    val windDescShort: String?,
    val beaufortScale: Int?,
    val beaufortDesc: String?,
    val uvIndex: Int?,
    val uvDesc: String?,
    val barometerPressure: Double?,
    val barometerTrend: String?,
    val visibility: Double?,
    val snowCover: Double?,
    val iconId: Int?,
    val iconName: String?,
    val iconLink: String?,
    val ageMinutes: Int?,
    val activeAlerts: Int?,
    val weekday: String?,
    @Serializable(with = ZonedDateTimeSerializer::class)
    val time: ZonedDateTime?,
)