package com.example.breathwatch.data.remote.model.extras

import com.squareup.moshi.Json

data class SpaceBodyDto(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "englishName")
    val englishName: String,
    @Json(name = "isPlanet")
    val isPlanet: Boolean,
    @Json(name = "moons")
    val moons: List<Moon>?,
    @Json(name = "semimajorAxis")
    val semimajorAxis: Int,
    @Json(name = "perihelion")
    val perihelion: Int,
    @Json(name = "aphelion")
    val aphelion: Int,
    @Json(name = "eccentricity")
    val eccentricity: Double,
    @Json(name = "inclination")
    val inclination: Double,
    @Json(name = "mass")
    val mass: Mass,
    @Json(name = "volume")
    val volume: Volume,
    @Json(name = "density")
    val density: Double,
    @Json(name = "gravity")
    val gravity: Double,
    @Json(name = "escape")
    val escape: Double,
    @Json(name = "meanRadius")
    val meanRadius: Double,
    @Json(name = "equaRadius")
    val equaRadius: Double,
    @Json(name = "polarRadius")
    val polarRadius: Double,
    @Json(name = "flattening")
    val flattening: Double,
    @Json(name = "dimension")
    val dimension: String,
    @Json(name = "sideralOrbit")
    val sideralOrbit: Double,
    @Json(name = "sideralRotation")
    val sideralRotation: Double,
    @Json(name = "aroundPlanet")
    val aroundPlanet: AroundPlanet?,
    @Json(name = "discoveredBy")
    val discoveredBy: String,
    @Json(name = "discoveryDate")
    val discoveryDate: String,
    @Json(name = "alternativeName")
    val alternativeName: String,
    @Json(name = "axialTilt")
    val axialTilt: Double,
    @Json(name = "avgTemp")
    val avgTemp: Int,
    @Json(name = "mainAnomaly")
    val mainAnomaly: Double,
    @Json(name = "argPeriapsis")
    val argPeriapsis: Double,
    @Json(name = "longAscNode")
    val longAscNode: Double,
    @Json(name = "bodyType")
    val bodyType: String,
    @Json(name = "rel")
    val rel: String
)

data class Moon(
    @Json(name = "moon")
    val moon: String,
    @Json(name = "rel")
    val rel: String
)

data class Mass(
    @Json(name = "massValue")
    val massValue: Double,
    @Json(name = "massExponent")
    val massExponent: Int
)

data class Volume(
    @Json(name = "volValue")
    val volValue: Double,
    @Json(name = "volExponent")
    val volExponent: Int
)

data class AroundPlanet(
    @Json(name = "planet")
    val planet: String,
    @Json(name = "rel")
    val rel: String
)
