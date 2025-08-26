package com.example.earthapp.model

data class WebcamsResponse(
    val total: Int?,
    val webcams: List<Webcam>?
)

data class Webcam(
    val id: String? = null,
    val webcamId: Long? = null,
    val title: String? = null,
    val image: ImageObj? = null,
    val images: ImageObj? = null
)

data class ImageObj(
    val current: CurrentImage? = null
)

data class CurrentImage(
    val preview: String? = null
)

