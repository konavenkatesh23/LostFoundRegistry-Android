package com.lostfound.api

import com.lostfound.model.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadApi {

    @Multipart
    @POST("api/upload-image")
    fun uploadImage(

        @Header("Authorization")
        token: String,

        @Part
        image: MultipartBody.Part

    ): Call<UploadResponse>

}