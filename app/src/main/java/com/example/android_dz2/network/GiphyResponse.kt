package com.example.android_dz2.network

data class GiphyResponse(
    val data: GifData
)

data class GifData (
    val id: String,
    val url: String,
    val images: GifImages
)

data class GifImages (
    val original: OriginalImage
)

data class OriginalImage (
    val url: String
)