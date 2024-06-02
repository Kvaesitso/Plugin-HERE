package de.mm20.launcher2.plugin.here.api

import kotlinx.serialization.Serializable

@Serializable
data class HAddress(
    val label: String?,
    val countryCode: String?,
    val countryName: String?,
    val state: String?,
    val stateCode: String?,
    val county: String?,
    val countyCode: String?,
    val city: String?,
    val district: String?,
    val subdistrict: String?,
    val street: String?,
    val streets: List<String>?,
    val block: String?,
    val subblock: String?,
    val postalCode: String?,
    val houseNumber: String?,
    val building: String?,
)
