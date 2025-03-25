package com.example.astroshare.data.remote.cloudinary

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface CloudinaryApi {

    // For an UNSIGNED upload, you pass 'upload_preset' in the form data
    @Multipart
    @POST("v1_1/{dssf5hhgl}/image/upload")
    suspend fun uploadImage(
        @Path("dssf5hhgl") cloudName: String,
        @Part file: MultipartBody.Part,
        @Part("upload_preset") uploadPreset: RequestBody
    ): CloudinaryResponse
}