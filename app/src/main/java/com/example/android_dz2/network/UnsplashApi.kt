package com.example.android_dz2.network

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface UnsplashApi {
    @Headers("Authorization: Client-ID ls5NgeJvHJKibvn4wrUGIJBMXHPkG_XEG4odh2Ce9qA")
    @GET("photos/")
    suspend fun getRandomPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<UnsplashPhoto>
}
