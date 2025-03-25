package com.example.astroshare.data.remote.cloudinary

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CloudinaryRetrofitClient {
    private const val BASE_URL = "https://api.cloudinary.com/"

    val instance: CloudinaryApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CloudinaryApi::class.java)
    }
}