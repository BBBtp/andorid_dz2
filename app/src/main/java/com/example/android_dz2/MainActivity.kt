package com.example.android_dz2

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.android_dz2.network.ApiClient
import com.example.android_dz2.network.UnsplashPhoto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate: Приложение запущено")
        enableEdgeToEdge()
        setContent {
            UnsplashPhotoScreen()

        }
    }
}

@Composable
fun UnsplashPhotoScreen() {
    val unsplashApi = ApiClient.unsplashApi

    val photos = remember { mutableStateListOf<UnsplashPhoto>() }
    var currentPage by remember { mutableIntStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    fun fetchPhotos(page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                isLoading = true
                errorMessage = ""
                val newPhotos = unsplashApi.getRandomPhotos(page = page, perPage = 30)
                photos.addAll(newPhotos)
                currentPage++
            } catch (e: Exception) {
                errorMessage = "Упс... Что-то пошло не так. Попробовать еще раз?"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchPhotos(currentPage)
    }

    if (isLoading && photos.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    } else if (errorMessage.isNotEmpty() && photos.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = errorMessage, modifier = Modifier.padding(16.dp))
                Button(onClick = { fetchPhotos(currentPage) }) {
                    Text("Повторить")
                }
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            state = rememberLazyGridState().also { state ->
                if (!isLoading && state.layoutInfo.visibleItemsInfo.lastOrNull()?.index == photos.size - 1) {
                    fetchPhotos(currentPage)
                }
            }
        ) {
            itemsIndexed(photos) { _, photo ->
                val imageHeight = photo.width.toFloat() / photo.height.toFloat()
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .aspectRatio(imageHeight)
                        .clip(RoundedCornerShape(15.dp))
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(photo.urls.regular),
                        contentDescription = "Photo from Unsplash",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

