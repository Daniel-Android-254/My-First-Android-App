package com.example.breathwatch.data.remote.model.extras

import com.squareup.moshi.Json

data class BookDto(
    @Json(name = "kind")
    val kind: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "etag")
    val etag: String,
    @Json(name = "selfLink")
    val selfLink: String,
    @Json(name = "volumeInfo")
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    @Json(name = "title")
    val title: String,
    @Json(name = "authors")
    val authors: List<String>,
    @Json(name = "publisher")
    val publisher: String,
    @Json(name = "publishedDate")
    val publishedDate: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "industryIdentifiers")
    val industryIdentifiers: List<IndustryIdentifier>,
    @Json(name = "readingModes")
    val readingModes: ReadingModes,
    @Json(name = "pageCount")
    val pageCount: Int,
    @Json(name = "printType")
    val printType: String,
    @Json(name = "categories")
    val categories: List<String>,
    @Json(name = "maturityRating")
    val maturityRating: String,
    @Json(name = "allowAnonLogging")
    val allowAnonLogging: Boolean,
    @Json(name = "contentVersion")
    val contentVersion: String,
    @Json(name = "panelizationSummary")
    val panelizationSummary: PanelizationSummary,
    @Json(name = "imageLinks")
    val imageLinks: ImageLinks,
    @Json(name = "language")
    val language: String,
    @Json(name = "previewLink")
    val previewLink: String,
    @Json(name = "infoLink")
    val infoLink: String,
    @Json(name = "canonicalVolumeLink")
    val canonicalVolumeLink: String
)

data class IndustryIdentifier(
    @Json(name = "type")
    val type: String,
    @Json(name = "identifier")
    val identifier: String
)

data class ReadingModes(
    @Json(name = "text")
    val text: Boolean,
    @Json(name = "image")
    val image: Boolean
)

data class PanelizationSummary(
    @Json(name = "containsEpubBubbles")
    val containsEpubBubbles: Boolean,
    @Json(name = "containsImageBubbles")
    val containsImageBubbles: Boolean
)

data class ImageLinks(
    @Json(name = "smallThumbnail")
    val smallThumbnail: String,
    @Json(name = "thumbnail")
    val thumbnail: String
)
