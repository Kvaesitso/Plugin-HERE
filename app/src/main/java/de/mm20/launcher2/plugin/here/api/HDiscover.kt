package de.mm20.launcher2.plugin.here.api

import kotlinx.serialization.Serializable

@Serializable
data class HDiscover(
    val items: List<HDiscoverItem>?,
)

@Serializable
data class HDiscoverItem(
    val title: String?,
    val id: String?,
    val address: HAddress?,
    val position: HPosition?,
    val categories: List<HCategory>?,
    val contacts: List<HContactsItem>?,
    val openingHours: List<HOpeningHours>?
)

@Serializable
data class HCategory(
    val id: String?,
    val name: String?,
    val primary: Boolean?,
)

@Serializable
data class HContactsItem(
    val phone: List<HContact>?,
    val mobile: List<HContact>?,
    val tollFree: List<HContact>?,
    val fax: List<HContact>?,
    val www: List<HContact>?,
    val email: List<HContact>?,
)

@Serializable
data class HContact(
    val label: String?,
    val value: String?,
)

@Serializable
data class HOpeningHours(
    val isOpen: Boolean?,
    val structured: List<HOpeningHoursStructured>?,
)

@Serializable
data class HOpeningHoursStructured(
    val start: String?,
    val duration: String?,
    val recurrence: String?,
)