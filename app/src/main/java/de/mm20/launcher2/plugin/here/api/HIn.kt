package de.mm20.launcher2.plugin.here.api

data class HIn(
    val lat: Double,
    val lon: Double,
    val r: Int,
) {
    override fun toString(): String {
        return "$lat,$lon;r=$r"
    }
}