package com.example.android_dz2.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://api.giphy.com/"

    val giphyApi: GiphyApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GiphyApi::class.java)
    }
}

suspend fun fetchRandomGifs(giphyApi: GiphyApi, apiKey: String): String {
    return try{
        val response = giphyApi.getRandomGif(apiKey)
        response.data.images.original.url
    } catch (e: Exception) {
        throw Exception("[NETWORK]:[Api Client]:[fetchRandomGifs] - Error: ${e.message}")
    }
}