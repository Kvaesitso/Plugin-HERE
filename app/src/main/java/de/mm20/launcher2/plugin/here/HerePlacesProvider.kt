package de.mm20.launcher2.plugin.here

import de.mm20.launcher2.plugin.config.QueryPluginConfig
import de.mm20.launcher2.plugin.config.StorageStrategy
import de.mm20.launcher2.plugin.here.api.HAddress
import de.mm20.launcher2.plugin.here.api.HDiscoverItem
import de.mm20.launcher2.plugin.here.api.HPosition
import de.mm20.launcher2.sdk.base.RefreshParams
import de.mm20.launcher2.sdk.base.SearchParams
import de.mm20.launcher2.sdk.locations.Location
import de.mm20.launcher2.sdk.locations.LocationProvider
import de.mm20.launcher2.sdk.locations.LocationQuery
import de.mm20.launcher2.search.location.Address
import de.mm20.launcher2.search.location.LocationIcon
import kotlin.time.Duration.Companion.days

class HerePlacesProvider : LocationProvider(
    config = QueryPluginConfig(
        storageStrategy = StorageStrategy.StoreCopy,
    )
) {
    private lateinit var apiClient: HereApiClient

    override fun onCreate(): Boolean {
        apiClient = HereApiClient(context!!.getString(R.string.api_key))
        return true
    }

    override suspend fun refresh(item: Location, params: RefreshParams): Location? {
        if ((System.currentTimeMillis() - params.lastUpdated) < 1.days.inWholeMilliseconds) {
            return item
        }
        return apiClient.lookup(item.id, lang = params.lang).toLocation()
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
    val category = categories?.firstOrNull { it.primary == true } ?: categories?.firstOrNull()
    return Location(
        id = id ?: return null,
        latitude = position?.lat ?: return null,
        longitude = position.lng ?: return null,
        category = category?.name,
        icon = category?.id?.let { getIcon(it) },
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

private fun getIcon(categoryId: String): LocationIcon? {
    return when (categoryId) {
        "100-1000-0000",
        "100-1000-0001",
        "100-1000-0002",
        "100-1000-0003",
        "100-1000-0004",
        "100-1000-0005",
        "100-1000-0006",
        "100-1000-0007",
        "100-1000-0008" -> LocationIcon.Restaurant

        "100-1000-0009" -> LocationIcon.FastFood
        "100-1100-0000",
        "100-1100-0010",
        "100-1100-0331" -> LocationIcon.Cafe

        "200-2000-0000", "200-2000-0012" -> LocationIcon.NightClub
        "200-2000-0011", "200-2000-0368" -> LocationIcon.Bar
        "200-2000-0013", "200-2000-0014", "200-2000-0015", "200-2000-0018" -> LocationIcon.ConcertHall
        "200-2100-0019" -> LocationIcon.MovieTheater
        "200-2200-0000", "200-2200-0020" -> LocationIcon.Theater
        "200-2300-0000", "200-2300-0021", "200-2300-0022" -> LocationIcon.Casino
        "300-3000-0024", "300-3100-0029" -> LocationIcon.ArtGallery
        "300-3000-0025", "300-3200-0000", "300-3200-0031", "300-3200-0309", "300-3200-0375" -> LocationIcon.Monument
        "300-3100-0000", "300-3100-0026", "300-3100-0027", "300-3100-0028" -> LocationIcon.Museum
        "300-3200-0030" -> LocationIcon.Museum
        "300-3200-0032" -> LocationIcon.Synagogue
        "300-3200-0033" -> LocationIcon.HinduTemple
        "300-3200-0034" -> LocationIcon.Mosque
        "350-3522-0239" -> LocationIcon.Forest
        "400-4000-4580", "400-4000-4581", "400-4000-4582" -> LocationIcon.Airport
        "400-4100-0035", "400-4100-0038", "400-4100-0039", "400-4100-0040", "400-4100-0339", "400-4100-0342" -> LocationIcon.Train
        "400-4100-0036", "400-4100-0042", "400-4100-0341" -> LocationIcon.Bus
        "400-4100-0037" -> LocationIcon.Subway
        "400-4100-0041", "400-4100-0047" -> LocationIcon.Taxi
        "400-4100-0043" -> LocationIcon.GenericTransit
        "400-4100-0044", "400-4100-0045", "400-4100-0046", "400-4100-0338" -> LocationIcon.Boat
        "400-4100-0226", "400-4100-0326" -> LocationIcon.Car
        "400-4100-0337" -> LocationIcon.Tram
        "400-4100-0340" -> LocationIcon.CableCar
        "400-4100-0347", "400-4100-0348" -> LocationIcon.Bike
        "400-4300-0200",
        "400-4300-0201",
        "700-7900-0131",
        "800-8500-0000",
        "800-8500-0177",
        "800-8500-0178",
        "800-8500-0179",
        "800-8500-0315" -> LocationIcon.Parking

        "400-4300-0202" -> LocationIcon.Motorcycle
        "500-5000-0000",
        "500-5000-0053",
        "500-5000-0054",
        "500-5100-0000",
        "500-5100-0055",
        "500-5100-0056",
        "500-5100-0057",
        "500-5100-0058",
        "500-5100-0059",
        "500-5100-0060" -> LocationIcon.Hotel

        "550-5510-0202" -> LocationIcon.Park
        "600-6000-0061" -> LocationIcon.ConvenienceStore
        "600-6100-0062" -> LocationIcon.ShoppingMall
        "600-6200-0063",
        "600-6300-0067",
        "600-6300-0245",
        "600-6300-0363",
        "600-6300-0364",
        "600-6400-0069",
        "600-6500-0072",
        "600-6500-0075",
        "600-6500-0076",
        "600-6600-0000",
        "600-6600-0077",
        "600-6600-0078",
        "600-6600-0079",
        "600-6600-0082",
        "600-6600-0083",
        "600-6600-0084",
        "600-6600-0085",
        "600-6600-0310",
        "600-6600-0319",
        "600-6900-0000",
        "600-6900-0094",
        "600-6900-0095",
        "600-6900-0096",
        "600-6900-0098",
        "600-6900-0099",
        "600-6900-0102",
        "600-6900-0103",
        "600-6900-0105",
        "600-6900-0106",
        "600-6900-0246",
        "600-6900-0247",
        "600-6900-0248",
        "600-6900-0249",
        "600-6900-0250",
        "600-6900-0251",
        "600-6900-0305",
        "600-6900-0307",
        "600-6900-0358",
        "600-6900-0388",
        "600-6900-0389",
        "600-6900-0390",
        "600-6900-0391",
        "600-6900-0392",
        "600-6900-0393",
        "600-6900-0394",
        "600-6900-0395",
        "600-6900-0396",
        "600-6900-0397",
        "600-6900-0398" -> LocationIcon.Shopping

        "600-6300-0064" -> LocationIcon.Restaurant
        "600-6300-0066" -> LocationIcon.Supermarket
        "600-6300-0068" -> LocationIcon.LiquorStore
        "600-6300-0244" -> LocationIcon.Bakery
        "600-6400-0000", "600-6400-0070" -> LocationIcon.Pharmacy
        "600-6500-0073", "600-6500-0074", "700-7100-0134" -> LocationIcon.CellPhoneStore
        "600-6600-0080" -> LocationIcon.FurnitureStore
        "600-6700-0000", "600-6700-0087" -> LocationIcon.BookStore
        "600-6800-0000",
        "600-6800-0089",
        "600-6800-0090",
        "600-6800-0091",
        "600-6800-0092",
        "600-6800-0093" -> LocationIcon.ClothingStore

        "600-6900-0097" -> LocationIcon.PetStore
        "600-6900-0100" -> LocationIcon.DiscountStore
        "600-6900-0101", "600-6900-0355" -> LocationIcon.Florist
        "600-6900-0356" -> LocationIcon.JewelryStore
        "600-6950-0000", "600-6950-0399", "600-6950-0401" -> LocationIcon.HairSalon

        "700-7000-0107", "700-7050-0109", "700-7050-0110" -> LocationIcon.Bank
        "700-7000-0108" -> LocationIcon.Atm
        "700-7300-0111", "700-7300-0112" -> LocationIcon.Police
        "700-7300-0113" -> LocationIcon.FireDepartment
        "700-7300-0280" -> LocationIcon.Hospital
        "700-7400-0289" -> LocationIcon.GovernmentBuilding
        "700-7450-0114" -> LocationIcon.PostOffice
        "700-7600-0000", "700-7600-0116" -> LocationIcon.GasStation
        "700-7600-0322" -> LocationIcon.ChargingStation
        "700-7850-0122", "700-7850-0123" -> LocationIcon.CarRepair
        "700-7851-0117" -> LocationIcon.CarRental
        "700-7900-0323" -> LocationIcon.CarWash
        "800-8000-0000" -> LocationIcon.Hospital
        "800-8000-0154" -> LocationIcon.Dentist
        "800-8000-0155" -> LocationIcon.Physician
        "800-8000-0159", "800-8000-0325" -> LocationIcon.Hospital
        "800-8000-0161" -> LocationIcon.Optician
        "800-8100-0000",
        "800-8100-0163",
        "800-8100-0164",
        "800-8100-0165",
        "800-8100-0168",
        "800-8100-0169",
        "800-8100-0171" -> LocationIcon.GovernmentBuilding

        "800-8100-0170" -> LocationIcon.Courthouse
        "800-8200-0000",
        "800-8200-0173",
        "800-8200-0174",
        "800-8200-0295",
        "800-8200-0360",
        "800-8200-0361",
        "800-8200-0362" -> LocationIcon.School

        "800-8300-0000" -> LocationIcon.Library
        "800-8300-0175" -> LocationIcon.Library

        "800-8600-0000",
        "800-8600-0181",
        "800-8600-0184",
        "800-8600-0187",
        "800-8600-0190",
        "800-8600-0196",
        "800-8600-0197",
        "800-8600-0192",
        "800-8600-0200",
        "800-8600-0316",
        "800-8600-0376",
        "800-8600-0377",
        "800-8600-0381" -> LocationIcon.Sports

        "800-8600-0180" -> LocationIcon.Stadium
        "800-8600-0182" -> LocationIcon.Swimming
        "800-8600-0183" -> LocationIcon.Tennis
        "800-8600-0185" -> LocationIcon.Skiing
        "800-8600-0186" -> LocationIcon.Hockey
        "800-8600-0189" -> LocationIcon.Soccer
        "800-8600-0193", "800-8600-0194" -> LocationIcon.Golf
        "800-8600-0199" -> LocationIcon.Basketball
        "800-8600-0314" -> LocationIcon.Rugby
        "800-8700-0198" -> LocationIcon.PublicBathroom

        else -> null
    }
}