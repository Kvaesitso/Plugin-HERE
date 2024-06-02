package de.mm20.launcher2.plugin.here

import android.util.Log
import de.mm20.launcher2.plugin.config.QueryPluginConfig
import de.mm20.launcher2.plugin.config.StorageStrategy
import de.mm20.launcher2.plugin.here.api.HAddress
import de.mm20.launcher2.plugin.here.api.HDiscoverItem
import de.mm20.launcher2.plugin.here.api.HPosition
import de.mm20.launcher2.sdk.base.GetParams
import de.mm20.launcher2.sdk.base.SearchParams
import de.mm20.launcher2.sdk.locations.Location
import de.mm20.launcher2.sdk.locations.LocationProvider
import de.mm20.launcher2.sdk.locations.LocationQuery
import de.mm20.launcher2.search.location.Address

class HerePlacesProvider : LocationProvider(
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
        return null
    }

    override suspend fun search(query: LocationQuery, params: SearchParams): List<Location> {
        if (!params.allowNetwork) return emptyList()
        return apiClient.discover(
            at = HPosition(
                lat = query.userLatitude,
                lng = query.userLongitude,
            ),
            q = query.query,
            lang = params.lang,
        ).items?.mapNotNull { it.toLocation() } ?: emptyList()
    }
}

private fun HDiscoverItem.toLocation(): Location? {
    return Location(
        id = id ?: return null,
        latitude = position?.lat ?: return null,
        longitude = position.lng ?: return null,
        category = categories?.firstOrNull { it.primary == true }?.name ?: categories?.firstOrNull()?.name,
        icon = null,
        label = title ?: return null,
        emailAddress = contacts?.asSequence()?.mapNotNull {
            it.email?.firstOrNull()?.value
        }?.firstOrNull(),
        phoneNumber = contacts?.asSequence()?.mapNotNull {
            it.phone?.firstOrNull()?.value
        }?.firstOrNull(),
        websiteUrl = contacts?.asSequence()?.mapNotNull {
            it.www?.firstOrNull()?.value
        }?.firstOrNull(),
        address = address?.toAddress(),

    )
}

private fun HAddress.toAddress(): Address {
    return Address(
        address = label,
        postalCode = postalCode,
        city = city,
        state = state,
        country = countryName,
    )
}