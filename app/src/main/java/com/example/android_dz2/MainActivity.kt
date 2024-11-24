package com.example.android_dz2

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.android_dz2.network.ApiClient
import com.example.android_dz2.network.GiphyApi
import com.example.android_dz2.network.fetchRandomGifs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate: Приложение запущено")
        enableEdgeToEdge()
        setContent {
                GiphyGifScreen()

        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
fun loadGif(
    giphyApi: GiphyApi,
    apiKey: String,
    onLoading: (Boolean) -> Unit,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    Log.d("loadGif", "Загрузка началась")
    onLoading(true)
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val gifUrl = fetchRandomGifs(giphyApi, apiKey)
            Log.d("loadGif", "Загрузка завершена: $gifUrl")
            onSuccess(gifUrl)
        } catch (e: Exception) {
            Log.e("loadGif", "Ошибка при загрузке: ${e.message}", e)
            onError(e.message ?: "Упс...Что-то пошло не так")
        } finally {
            onLoading(false)
            Log.d("loadGif", "Загрузка завершена")
        }
    }
}

@Composable
fun GiphyGifScreen() {
    val apiKey = "dc6zaTOxFJmzC"
    val giphyApi = ApiClient.giphyApi

    var gifUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        Log.d("GiphyGifScreen", "Запуск LaunchedEffect")
        loadGif(
            giphyApi = giphyApi,
            apiKey = apiKey,
            onLoading = { isLoading = it },
            onSuccess = { gifUrl = it },
            onError = { errorMessage = it }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            isLoading -> {
                Log.d("GiphyGifScreen", "Состояние: загрузка")
                Text("Загрузка GIF...")
            }
            errorMessage.isNotEmpty() -> {
                Log.d("GiphyGifScreen", "Состояние: ошибка $errorMessage")
                Text("Ошибка: $errorMessage")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    Log.d("GiphyGifScreen", "Повторная попытка загрузки")
                    loadGif(
                        giphyApi = giphyApi,
                        apiKey = apiKey,
                        onLoading = { isLoading = it },
                        onSuccess = { gifUrl = it },
                        onError = { errorMessage = it }
                    )
                }) {
                    Text("Повторить загрузку")
                }
            }
            gifUrl.isNotEmpty() -> {
                Log.d("GiphyGifScreen", "Состояние: изображение загружено $gifUrl")
                Image(
                    painter = rememberAsyncImagePainter(model = gifUrl),
                    contentDescription = "GIF",
                    modifier = Modifier.size(300.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    Log.d("GiphyGifScreen", "Загрузка другого GIF")
                    loadGif(
                        giphyApi = giphyApi,
                        apiKey = apiKey,
                        onLoading = { isLoading = it },
                        onSuccess = { gifUrl = it },
                        onError = { errorMessage = it }
                    )
                }) {
                    Text("Показать другой GIF")
                }
            }
        }
    }
}
