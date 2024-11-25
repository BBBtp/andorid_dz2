package com.example.android_dz2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_dz2.network.ApiClient
import com.example.android_dz2.network.UnsplashPhoto
import kotlinx.coroutines.launch

class UnsplashPhotoViewModel : ViewModel() {
    private val unsplashApi = ApiClient.unsplashApi

    val photos = mutableStateListOf<UnsplashPhoto>()
    private var currentPage by mutableIntStateOf(1)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    fun fetchPhotos() {
        if (isLoading) return
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = ""
                val newPhotos = unsplashApi.getRandomPhotos(page = currentPage, perPage = 30)
                photos.addAll(newPhotos)
                currentPage++
            } catch (e: Exception) {
                errorMessage = "Упс... Что-то пошло не так. Попробовать еще раз?"
            } finally {
                isLoading = false
            }
        }
    }
}