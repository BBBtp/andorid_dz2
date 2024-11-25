package com.example.android_dz2.network

import androidx.compose.ui.text.font.FontWeight

data class UnsplashPhoto(
    val id: String,
    val width: Int,
    val height: Int,
    val urls: PhotoUrls
)

data class PhotoUrls(
    val full: String,
    val regular: String,
    val small: String
)